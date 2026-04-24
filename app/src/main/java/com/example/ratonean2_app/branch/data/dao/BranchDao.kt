package com.example.ratonean2_app.branch.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.ratonean2_app.branch.data.entity.BranchEntity
import com.example.ratonean2_app.branch.domain.model.Branch
import kotlinx.coroutines.flow.Flow

@Dao
interface BranchDao {

    @Query("SELECT * FROM branches")
    fun getAll(): Flow<List<BranchEntity>>

    @Query("SELECT * FROM branches WHERE branchId = :id LIMIT 1")
    fun getById(id: String): Flow<BranchEntity?>

    @Query("SELECT * FROM branches WHERE commerceId = :commerceId")
    fun getByCommerce(commerceId: String): Flow<List<BranchEntity>>

    @Query("""
        SELECT * FROM branches
        WHERE latitude BETWEEN :minLat AND :maxLat
        AND longitude BETWEEN :minLon AND :maxLon
    """)
    fun getNearbyBoundingBox(
        minLat: Double,
        maxLat: Double,
        minLon: Double,
        maxLon: Double
    ): Flow<List<BranchEntity>>

    fun getNearby(
        latitude: Double,
        longitude: Double,
        radiusKm: Double
    ): Flow<List<BranchEntity>> {
        val latDelta = radiusKm / 111.0
        val lonDelta = radiusKm / (111.0 * kotlin.math.cos(Math.toRadians(latitude)))

        return getNearbyBoundingBox(
            minLat = latitude - latDelta,
            maxLat = latitude + latDelta,
            minLon = longitude - lonDelta,
            maxLon = longitude + lonDelta
        )
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(list: List<BranchEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(branch: BranchEntity)

    @Query("DELETE FROM branches")
    suspend fun clear()

    @Query("DELETE FROM branches WHERE branchId = :id")
    suspend fun deleteById(id: String)

    @Query("SELECT COUNT(*) FROM branches")
    suspend fun count(): Int
}