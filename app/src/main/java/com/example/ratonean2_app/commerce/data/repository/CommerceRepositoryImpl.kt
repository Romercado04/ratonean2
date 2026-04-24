package com.example.ratonean2_app.commerce.data.repository

import android.util.Log
import com.example.ratonean2_app.commerce.data.entity.toDomain
import com.example.ratonean2_app.commerce.data.entity.toEntity
import com.example.ratonean2_app.commerce.data.local.CommerceLocalDataSource
import com.example.ratonean2_app.commerce.domain.model.Commerce
import com.example.ratonean2_app.commerce.domain.provider.CommerceProvider
import com.example.ratonean2_app.commerce.domain.repository.CommerceRepository
import com.example.ratonean2_app.core.network.NetworkResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

class CommerceRepositoryImpl(
    private val localDataSource: CommerceLocalDataSource,
    private val remoteProvider: CommerceProvider
) : CommerceRepository {

    private val TAG = "CommerceRepository"

    override fun getAllCommerces(): Flow<List<Commerce>> = flow {
        var hasLocal = false
        try {
            val local = localDataSource.getAllCommerces().first().map { it.toDomain() }
            if (local.isNotEmpty()) {
                Log.d(TAG, "📦 Room: ${local.size} comercios")
                emit(local)
                hasLocal = true
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error Room: ${e.message}")
        }

        remoteProvider.getAllCommerces().collect { response ->
            if (response is NetworkResponse.Success) {
                val remote = response.data.orEmpty()
                if (remote.isNotEmpty()) {
                    localDataSource.insertCommerces(remote.map { it.toEntity() })
                    emit(remote)
                }
            } else if (response is NetworkResponse.Failure && !hasLocal) {
                emit(emptyList())
            }
        }
    }.catch { emit(emptyList()) }

    override fun getCommerceById(id: String): Flow<Commerce?> = flow<Commerce?> {
        val local = localDataSource.getCommerceById(id).first()?.toDomain()
        if (local != null) emit(local)

        remoteProvider.getCommerceById(id).collect { response ->
            if (response is NetworkResponse.Success) {
                response.data?.let {
                    localDataSource.insertCommerce(it.toEntity())
                    emit(it)
                }
            }
        }
    }.catch { e ->
        Log.e(TAG, "❌ Error getCommerceById: ${e.message}")
        emit(null)
    }
}