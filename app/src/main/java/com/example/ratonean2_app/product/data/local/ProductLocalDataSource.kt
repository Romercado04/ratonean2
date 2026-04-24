package com.example.ratonean2_app.product.data.local

import com.example.ratonean2_app.product.data.dao.ProductDao
import com.example.ratonean2_app.product.data.entity.ProductEntity
import kotlinx.coroutines.flow.Flow

class ProductLocalDataSource(
    private val productDao: ProductDao
) {
    fun getProductsByBranch(branchId: String): Flow<List<ProductEntity>> =
        productDao.getByBranch(branchId)

    fun getAllProducts(): Flow<List<ProductEntity>> =
        productDao.getAll()

    suspend fun insertProducts(products: List<ProductEntity>) {
        productDao.insertAll(products)
    }

    suspend fun insertProduct(product: ProductEntity) {
        productDao.insertAll(listOf(product))
    }

    suspend fun clearCache() {
        productDao.clear()
    }
}