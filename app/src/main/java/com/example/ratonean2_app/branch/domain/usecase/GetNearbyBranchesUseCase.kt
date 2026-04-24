package com.example.ratonean2_app.branch.domain.usecase

import com.example.ratonean2_app.branch.domain.provider.BranchProvider
import com.example.ratonean2_app.branch.domain.repository.BranchRepository

class GetNearbyBranchesUseCase(private val repository: BranchRepository) {
    operator fun invoke(lat: Double, lon: Double, dist: Double) =
        repository.getNearbyBranches(lat, lon, dist)
}