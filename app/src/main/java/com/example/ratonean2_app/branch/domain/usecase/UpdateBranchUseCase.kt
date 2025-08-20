package com.example.ratonean2_app.branch.domain.usecase

import com.example.ratonean2_app.branch.domain.model.Branch
import com.example.ratonean2_app.branch.domain.provider.BranchProvider

class UpdateBranchUseCase(private val branchProvider: BranchProvider) {
    operator fun invoke(branch: Branch) = branchProvider.updateBranch(branch)
}