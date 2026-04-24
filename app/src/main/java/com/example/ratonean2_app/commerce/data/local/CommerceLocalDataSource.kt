package com.example.ratonean2_app.commerce.data.local

import com.example.ratonean2_app.commerce.data.dao.CommerceDao
import com.example.ratonean2_app.commerce.data.entity.CommerceEntity
import kotlinx.coroutines.flow.Flow

class CommerceLocalDataSource(private val dao: CommerceDao) {
    fun getAllCommerces(): Flow<List<CommerceEntity>> = dao.getAll()

    fun getCommerceById(id: String): Flow<CommerceEntity?> = dao.getById(id)

    suspend fun insertCommerces(commerces: List<CommerceEntity>) = dao.insertAll(commerces)

    suspend fun insertCommerce(commerce: CommerceEntity) = dao.insert(commerce)
}

