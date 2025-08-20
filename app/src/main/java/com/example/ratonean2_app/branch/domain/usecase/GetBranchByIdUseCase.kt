package com.example.ratonean2_app.branch.domain.usecase

import com.example.ratonean2_app.branch.domain.provider.BranchProvider

class GetBranchByIdUseCase(private val branchProvider: BranchProvider) {
    operator fun invoke(id: String) = branchProvider.getBranchById(id)
}