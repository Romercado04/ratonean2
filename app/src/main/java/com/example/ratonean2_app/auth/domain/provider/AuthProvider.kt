package com.example.ratonean2_app.auth.domain.provider

import com.example.ratonean2_app.auth.data.dto.JwtResponse

interface AuthProvider {
    suspend fun loginWithGoogle(token: String): JwtResponse
    suspend fun loginWithEmail(email: String, password: String): String
    suspend fun createUserWithEmail(name: String, email: String, password:String?)
}