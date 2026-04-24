package com.example.ratonean2_app.commerce.domain.repository

import com.example.ratonean2_app.commerce.domain.model.Commerce
import kotlinx.coroutines.flow.Flow

interface CommerceRepository {
    fun getAllCommerces(): Flow<List<Commerce>>
    fun getCommerceById(id: String): Flow<Commerce?>
}