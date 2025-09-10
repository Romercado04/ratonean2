package com.example.ratonean2_app.product.domain.usecase

import com.example.ratonean2_app.product.domain.provider.ProductProvider

class GetProductsByBranchUseCase(private val productRepository: ProductProvider) {
    operator fun invoke(branchId: String) = productRepository.getProductsByBranch(branchId)
}