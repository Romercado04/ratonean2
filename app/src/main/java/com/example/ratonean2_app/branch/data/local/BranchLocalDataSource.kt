package com.example.ratonean2_app.branch.data.local

import com.example.ratonean2_app.branch.data.dao.BranchDao
import com.example.ratonean2_app.branch.data.entity.BranchEntity
import kotlinx.coroutines.flow.Flow

class BranchLocalDataSource(
    private val dao: BranchDao
) {

    fun getAllBranches(): Flow<List<BranchEntity>> =
        dao.getAll()

    fun getBranchById(id: String): Flow<BranchEntity?> =
        dao.getById(id)

    fun getNearbyBranches(
        latitude: Double,
        longitude: Double,
        distance: Double
    ): Flow<List<BranchEntity>> =
        dao.getNearby(latitude, longitude, distance) // si no lo tenés, después lo armamos

    suspend fun insertBranches(branches: List<BranchEntity>) {
        dao.insertAll(branches)
    }

    suspend fun insertBranch(branch: BranchEntity) {
        dao.insert(branch)
    }

    suspend fun clearBranches() {
        dao.clear()
    }

    suspend fun deleteBranch(id: String) {
        dao.deleteById(id)
    }
}