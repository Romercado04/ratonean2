package com.example.ratonean2_app.branch.data.repository

import android.util.Log
import com.example.ratonean2_app.branch.data.entity.toDomain
import com.example.ratonean2_app.branch.data.local.BranchLocalDataSource
import com.example.ratonean2_app.branch.domain.model.Branch
import com.example.ratonean2_app.branch.domain.model.toEntity
import com.example.ratonean2_app.branch.domain.provider.BranchProvider
import com.example.ratonean2_app.branch.domain.repository.BranchRepository
import com.example.ratonean2_app.core.network.NetworkResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

class BranchRepositoryImpl(
    private val localDataSource: BranchLocalDataSource,
    private val remoteProvider: BranchProvider
) : BranchRepository {

    private val TAG = "BranchRepository"

    override fun getNearbyBranches(
        lat: Double,
        lon: Double,
        distance: Double
    ): Flow<List<Branch>> = flow {

        var localBranches: List<Branch> = emptyList()
        try {
            localBranches = localDataSource.getNearbyBranches(lat, lon, distance)
                .first()
                .map { it.toDomain() }

            Log.d(TAG, "📦 Room: Despachando ${localBranches.size} sucursales inmediatamente")
            emit(localBranches)
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error Room: ${e.message}")
        }

        remoteProvider.getNearbyBranches(lat, lon, distance).collect { response ->
            if (response is NetworkResponse.Success) {
                val remoteBranches = response.data.orEmpty()
                if (remoteBranches.isNotEmpty()) {
                    Log.d(TAG, "🌐 API: Llegó data fresca, actualizando Room y UI")
                    try {
                        localDataSource.insertBranches(remoteBranches.map { it.toEntity() })
                        emit(remoteBranches)
                    } catch (e: Exception) {
                        Log.e(TAG, "❌ Error al persistir API: ${e.message}")
                        emit(remoteBranches)
                    }
                }
            } else if (response is NetworkResponse.Failure) {
                Log.w(TAG, "⚠️ API falló (o timeout). El usuario sigue viendo Room.")
                if (localBranches.isEmpty()) emit(emptyList())
            }
        }
    }.catch { e ->
        Log.e(TAG, "🚨 Error crítico en Repository: ${e.message}")
        emit(emptyList())
    }

    override fun getBranchById(id: String): Flow<Branch?> = flow {
        var foundInLocal = false

        try {
            val local = localDataSource.getBranchById(id).first()?.toDomain()
            if (local != null) {
                Log.d(TAG, "📦 Room: Detalle de sucursal $id despachado")
                emit(local)
                foundInLocal = true
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error Room getBranchById: ${e.message}")
        }

        remoteProvider.getBranchById(id).collect { response ->
            if (response is NetworkResponse.Success) {
                response.data?.let { remoteBranch ->
                    Log.d(TAG, "🌐 API: Detalle fresco de $id recibido")
                    try {
                        localDataSource.insertBranch(remoteBranch.toEntity())
                        emit(remoteBranch) // Segunda emisión: pisa con lo más nuevo
                    } catch (e: Exception) {
                        Log.e(TAG, "❌ Error persistiendo branch: ${e.message}")
                        emit(remoteBranch)
                    }
                }
            } else if (response is NetworkResponse.Failure && !foundInLocal) {
                Log.w(TAG, "⚠️ API falló y no había nada local para $id")
                emit(null)
            }
        }
    }.catch { e ->
        Log.e(TAG, "🚨 Error crítico getBranchById: ${e.message}")
        emit(null)
    }

}