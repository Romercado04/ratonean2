package com.example.ratonean2_app.product.domain.usecase

import com.example.ratonean2_app.product.domain.provider.ProductProvider

class GetPopularProductsUseCase(private val productProvider: ProductProvider) {
    operator fun invoke(branchId: List<String>, limit: Int? = null) = productProvider.getPopularProducts(branchId, limit)
}