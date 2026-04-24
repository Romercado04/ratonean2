package com.example.ratonean2_app.product.data.repository

import android.util.Log
import com.example.ratonean2_app.core.network.NetworkResponse
import com.example.ratonean2_app.product.data.entity.toDomain
import com.example.ratonean2_app.product.data.entity.toEntity
import com.example.ratonean2_app.product.data.local.ProductLocalDataSource
import com.example.ratonean2_app.product.domain.model.Product
import com.example.ratonean2_app.product.domain.provider.ProductProvider
import com.example.ratonean2_app.product.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

class ProductRepositoryImpl(
    private val localDataSource: ProductLocalDataSource,
    private val productProvider: ProductProvider
) : ProductRepository {

    private val TAG = "ProductRepository"

    override fun getProductsByBranch(branchId: String): Flow<List<Product>> = flow {
        var hasLocal = false
        try {
            val local = localDataSource.getProductsByBranch(branchId).first().map { it.toDomain() }
            if (local.isNotEmpty()) {
                Log.d(TAG, "📦 Room: ${local.size} sucursales")
                emit(local)
                hasLocal = true
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error local: ${e.message}")
        }

        productProvider.getProductsByBranch(branchId).collect { response ->
            if (response is NetworkResponse.Success) {
                val remote = response.data.orEmpty()
                if (remote.isNotEmpty()) {
                    localDataSource.insertProducts(remote.map { it.toEntity() })
                    emit(remote)
                }
            } else if (response is NetworkResponse.Failure && !hasLocal) {
                emit(emptyList())
            }
        }
    }.catch { emit(emptyList()) }

    override fun getProductsBySearchInBranches(
        branchIds: List<String>,
        query: String
    ): Flow<List<Product>> = flow {
        productProvider.getProductsBySearchInBranches(branchIds, query).collect { response ->
            if (response is NetworkResponse.Success) {
                val remote = response.data.orEmpty()
                localDataSource.insertProducts(remote.map { it.toEntity() })
                emit(remote)
            } else if (response is NetworkResponse.Failure) {
                val fallback = localDataSource.getAllProducts().first()
                    .filter { it.branchId in branchIds && it.description.contains(query, true) }
                    .map { it.toDomain() }
                emit(fallback)
            }
        }
    }.catch { emit(emptyList()) }

    override fun getPopularProducts(branchIds: List<String>, limit: Int?): Flow<List<Product>> = flow {
        val local = localDataSource.getAllProducts().first()
            .filter { it.branchId in branchIds }
            .take(20)
            .map { it.toDomain() }

        if (local.isNotEmpty()) emit(local)

        productProvider.getPopularProducts(branchIds, limit).collect { response ->
            if (response is NetworkResponse.Success) {
                val remote = response.data.orEmpty()
                localDataSource.insertProducts(remote.map { it.toEntity() })
                emit(remote)
            }
        }
    }.catch { emit(emptyList()) }

    override fun getProductById(id: String): Flow<Product?> = flow<Product?> {
        val local = localDataSource.getAllProducts().first()
            .find { it.productId == id }?.toDomain()

        if (local != null) {
            emit(local)
        }

        productProvider.getProductById(id).collect { response ->
            if (response is NetworkResponse.Success) {
                response.data?.let { product ->
                    localDataSource.insertProducts(listOf(product.toEntity()))
                    emit(product)
                }
            }
        }
    }.catch { e ->
        Log.e(TAG, "Error getProductById: ${e.message}")
        emit(null)
    }

    override fun getProductsWithPromos(branchId: String, brand: String?): Flow<List<Product>> = flow {
        productProvider.getProductsWithPromos(branchId, brand).collect { response ->
            if (response is NetworkResponse.Success) {
                val remote = response.data.orEmpty()
                localDataSource.insertProducts(remote.map { it.toEntity() })
                emit(remote)
            }
        }
    }.catch { emit(emptyList()) }
}