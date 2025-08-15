package com.example.ratonean2_app.product.domain.usecase

import com.example.ratonean2_app.product.domain.model.Product
import com.example.ratonean2_app.product.domain.provider.ProductProvider

class UpdateProduct (private val productProvider: ProductProvider) {
    operator fun invoke(product: Product) = productProvider.updateProduct(product)
}