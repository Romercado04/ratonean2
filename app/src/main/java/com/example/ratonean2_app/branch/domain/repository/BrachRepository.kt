package com.example.ratonean2_app.branch.domain.repository

import com.example.ratonean2_app.branch.domain.model.Branch
import com.example.ratonean2_app.core.network.NetworkResponse
import kotlinx.coroutines.flow.Flow

interface BranchRepository {

    fun getBranchById(id: String): Flow<Branch?>
    fun getNearbyBranches(lat: Double, lon: Double, distance: Double): Flow<List<Branch>>
}