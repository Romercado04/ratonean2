package com.example.ratonean2_app.user.domain.model

data class UserLocation(
    val latitude: Double,
    val longitude: Double,
    val addressName: String? = null,
    val isGpsBased: Boolean = false
)