package com.example.ratonean2_app.product.data.provider

import com.example.ratonean2_app.core.network.ApiUrls
import com.example.ratonean2_app.core.network.NetworkResponse
import com.example.ratonean2_app.product.domain.model.Product
import com.example.ratonean2_app.product.domain.provider.ProductProvider
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.isSuccess
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ProductProviderImpl(private val client: HttpClient) : ProductProvider {
    override fun getAllProducts(): Flow<NetworkResponse<List<Product>>> = flow {
        emit(NetworkResponse.Loading())
        try {
            val response = client.get(ApiUrls.PRODUCTS)
            if (response.status.isSuccess()) {
                val products = response.body<List<Product>>()
                emit(NetworkResponse.Success(products))
            } else {
                emit(NetworkResponse.Failure(response.status.description))
            }
        } catch (e: Exception) {
            emit(NetworkResponse.Failure(e.message ?: "Unknown error"))
        }
    }

    override fun getProductById(id: String): Flow<NetworkResponse<Product>> = flow {
        try {
            emit(NetworkResponse.Loading())
            val response = client.get(ApiUrls.PRODUCTS_BY_ID.replace("{id}", id))
            if (response.status.isSuccess()) {
                val product = response.body<Product>()
                emit(NetworkResponse.Success(product))
            } else {
                emit(NetworkResponse.Failure(response.status.description))
            }
        } catch (e: Exception) {
            emit(NetworkResponse.Failure(e.message ?: "Unknown error"))
        }
    }

    override fun createProduct(product: Product): Flow<NetworkResponse<Unit>> {
        TODO("Not yet implemented")
    }

    override fun updateProduct(product: Product): Flow<NetworkResponse<Unit>> {
        TODO("Not yet implemented")
    }

    override fun deleteProduct(id: String): Flow<NetworkResponse<Unit>> {
        TODO("Not yet implemented")
    }
}