package com.example.ratonean2_app.product.domain.repository

import com.example.ratonean2_app.product.domain.model.Product
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    fun getProductsByBranch(branchId: String): Flow<List<Product>>
    fun getProductsBySearchInBranches(branchIds: List<String>, query: String): Flow<List<Product>>
    fun getPopularProducts(branchIds: List<String>, limit: Int?): Flow<List<Product>>
    fun getProductById(id: String): Flow<Product?>
    fun getProductsWithPromos(branchId: String, brand: String?): Flow<List<Product>>
}