package com.example.ratonean2_app.product.domain.usecase

import com.example.ratonean2_app.product.domain.repository.ProductRepository

class GetProductsBySearchInBranches(private val repository: ProductRepository) {
    operator fun invoke(branchIds: List<String>, query: String) =
        repository.getProductsBySearchInBranches(branchIds, query)
}