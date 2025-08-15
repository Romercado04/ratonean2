package com.example.ratonean2_app.product.domain.provider

import com.example.ratonean2_app.core.network.NetworkResponse
import com.example.ratonean2_app.product.domain.model.Product
import kotlinx.coroutines.flow.Flow

interface ProductProvider {
    fun getAllProducts(): Flow<NetworkResponse<List<Product>>>
    fun getProductById(id: String): Flow<NetworkResponse<Product>>
    fun createProduct(product: Product): Flow<NetworkResponse<Unit>>
    fun updateProduct(product: Product): Flow<NetworkResponse<Unit>>
    fun deleteProduct(id: String): Flow<NetworkResponse<Unit>>
}