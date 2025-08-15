package com.example.ratonean2_app.product.domain.usecase

import com.example.ratonean2_app.product.domain.provider.ProductProvider

class GetAllProductsUseCase(private val productProvider: ProductProvider) {
    operator fun invoke() = productProvider.getAllProducts()
}