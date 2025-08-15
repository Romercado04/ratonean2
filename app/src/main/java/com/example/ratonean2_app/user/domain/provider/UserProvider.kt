package com.example.ratonean2_app.user.domain.provider

import com.example.ratonean2_app.core.network.NetworkResponse
import com.example.ratonean2_app.user.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserProvider {
    //fun createNewUser is created in AuthProvider
    fun getUsers(): Flow<NetworkResponse<List<User>>>
    fun getUserById(id: String): Flow<NetworkResponse<User>>
    fun getUSerByEmail(email: String): Flow<NetworkResponse<User>>
    fun deleteUser(id: String): Flow<NetworkResponse<Unit>>
    fun updateUser(userUpdate: User): Flow<NetworkResponse<Unit>>
}