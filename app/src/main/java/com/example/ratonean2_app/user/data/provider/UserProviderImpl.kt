package com.example.ratonean2_app.user.data.provider

import com.example.ratonean2_app.core.network.ApiUrls
import com.example.ratonean2_app.core.network.NetworkResponse
import com.example.ratonean2_app.user.domain.model.User
import com.example.ratonean2_app.user.domain.provider.UserProvider
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class UserProviderImpl(private val client: HttpClient) : UserProvider {
    override fun getUsers(): Flow<NetworkResponse<List<User>>> = flow {
        emit(NetworkResponse.Loading())
        try {
            val response = client.get(ApiUrls.USERS)
            if (response.status.isSuccess()) {
                val users = response.body<List<User>>()
                emit(NetworkResponse.Success(users))
            } else {
                emit(NetworkResponse.Failure(response.status.description))
            }
        } catch (e: Exception) {
            emit(NetworkResponse.Failure(e.message ?: "Unknown error"))
        }
    }

    override fun getUserById(id: String): Flow<NetworkResponse<User>> = flow {
        try {
            emit(NetworkResponse.Loading())
            val response = client.get(ApiUrls.USER_BY_ID.replace("{id}", id))
            if (response.status.isSuccess()) {
                val user = response.body<User>()
                emit(NetworkResponse.Success(user))
            } else {
                emit(NetworkResponse.Failure(response.status.description))
            }
        } catch (e: Exception) {
            emit(NetworkResponse.Failure(e.message ?: "Unknown error"))
        }
    }

    override fun getUSerByEmail(email: String): Flow<NetworkResponse<User>> = flow {
        try {
            emit(NetworkResponse.Loading())
            val response = client.get(ApiUrls.USER_BY_EMAIL.replace("{email}", email))
            if (response.status.isSuccess()) {
                val user = response.body<User>()
                emit(NetworkResponse.Success(user))
            } else {
                emit(NetworkResponse.Failure(response.status.description))
            }
        } catch (e: Exception) {
            emit(NetworkResponse.Failure(e.message ?: "Unknown error"))
        }
    }

    override fun deleteUser(id: String): Flow<NetworkResponse<Unit>> = flow {
        try {
            emit(NetworkResponse.Loading())
            val response = client.delete(ApiUrls.USER_BY_ID.replace("{id}", id))
            if (response.status.isSuccess()) {
                emit(NetworkResponse.Success(Unit))
            } else {
                emit(NetworkResponse.Failure(response.status.description))
            }
        } catch (e: Exception) {
            emit(NetworkResponse.Failure(e.message ?: "Unknown error"))
        }
    }

    override fun updateUser(userUpdate: User): Flow<NetworkResponse<Unit>> = flow {
        try {
            emit(NetworkResponse.Loading())
            val response = client.put(ApiUrls.USER_BY_ID.replace("{id}", userUpdate.id)) {
                contentType(ContentType.Application.Json)
                setBody(userUpdate)
            }
            if (response.status.isSuccess()) {
                emit(NetworkResponse.Success(Unit))
            } else {
                emit(NetworkResponse.Failure(response.status.description))
            }
        } catch (e: Exception) {
            emit(NetworkResponse.Failure(e.message ?: "Unknown error"))
        }
    }

}