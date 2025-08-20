package com.example.ratonean2_app.product.domain.usecase

import com.example.ratonean2_app.product.domain.provider.ProductProvider

class GetProductByIdUseCase (private val productProvider: ProductProvider) {
    operator fun invoke(id: String) = productProvider.getProductById(id)
}