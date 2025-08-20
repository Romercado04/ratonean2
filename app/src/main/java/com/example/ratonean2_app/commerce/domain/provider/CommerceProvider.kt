package com.example.ratonean2_app.commerce.domain.provider

import com.example.ratonean2_app.commerce.domain.model.Commerce
import com.example.ratonean2_app.core.network.NetworkResponse
import kotlinx.coroutines.flow.Flow

interface CommerceProvider {
    fun getAllCommerces(): Flow<NetworkResponse<List<Commerce>>>
    fun getCommerceById(id: String): Flow<NetworkResponse<Commerce>>
    fun createCommerce(commerce: Commerce): Flow<NetworkResponse<Unit>>
    fun updateCommerce(commerce: Commerce): Flow<NetworkResponse<Unit>>
    fun deleteCommerce(id: String): Flow<NetworkResponse<Unit>>
}