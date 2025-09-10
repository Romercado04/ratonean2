package com.example.ratonean2_app.product.domain.usecase

import com.example.ratonean2_app.product.domain.provider.ProductProvider

class GetProductsWithPromosUseCase(private val productProvider: ProductProvider) {
    operator fun invoke(branchId: String, brand: String?) = productProvider.getProductsWithPromos(branchId, brand)
}