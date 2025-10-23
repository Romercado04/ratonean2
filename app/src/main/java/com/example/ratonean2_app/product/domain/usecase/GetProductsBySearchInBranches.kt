package com.example.ratonean2_app.product.domain.usecase

import com.example.ratonean2_app.product.domain.provider.ProductProvider

class GetProductsBySearchInBranches(private val productProvider: ProductProvider) {
    suspend operator fun invoke(branchIds: List<String>, query: String) = productProvider.getProductsBySearchInBranches(branchIds, query)
}