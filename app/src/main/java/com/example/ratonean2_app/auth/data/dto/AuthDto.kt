package com.example.ratonean2_app.auth.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class GoogleTokenRequest(val token: String)

@Serializable
data class JwtResponse(val token: String)