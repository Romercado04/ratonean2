package com.example.ratonean2_app.auth.data.provider

import com.example.ratonean2_app.auth.data.dto.CreateUserRequest
import com.example.ratonean2_app.auth.data.dto.GoogleTokenRequest
import com.example.ratonean2_app.auth.data.dto.JwtResponse
import com.example.ratonean2_app.auth.domain.provider.AuthProvider
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.request.*
import io.ktor.client.statement.bodyAsText
import io.ktor.http.*

class AuthProviderImpl(private val client: HttpClient): AuthProvider {
    override suspend fun loginWithGoogle(token: String): JwtResponse {
        return try {
            client.post("http://10.0.2.2:8080/auth/google") {
                contentType(ContentType.Application.Json)
                setBody(GoogleTokenRequest(token))
            }.body()
        } catch (e: ClientRequestException) { // 4xx
            val errorText = e.response.bodyAsText()
            throw Exception("Error al loguear: $errorText")
        }
    }

    override suspend fun loginWithEmail(
        email: String,
        password: String
    ): String {
        TODO("Not yet implemented")
    }

    override suspend fun createUserWithEmail(name: String, email: String, password: String?) {
        client.post("http://10.0.2.2:8080/users") {
            contentType(ContentType.Application.Json)
            setBody(CreateUserRequest(name, email, password))
        }
    }

}