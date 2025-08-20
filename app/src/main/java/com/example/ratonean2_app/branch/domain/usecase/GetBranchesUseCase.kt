package com.example.ratonean2_app.branch.domain.usecase

import com.example.ratonean2_app.branch.domain.provider.BranchProvider

class GetBranchesUseCase(private val branchProvider: BranchProvider) {
    operator fun invoke() = branchProvider.getAllBranches()
}