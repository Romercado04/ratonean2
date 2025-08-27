package com.example.ratonean2_app.branch.domain.usecase

import com.example.ratonean2_app.branch.domain.provider.BranchProvider

class GetNearbyBranchesUseCase(private val branchProvider: BranchProvider) {
    operator fun invoke(latitude: Double, longitude: Double, distance: Double) = branchProvider.getNearbyBraches(latitude, longitude, distance)
}