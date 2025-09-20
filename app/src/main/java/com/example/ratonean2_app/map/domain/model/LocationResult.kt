package com.example.ratonean2_app.map.domain.model

sealed class LocationResult {
    object PermissionDenied : LocationResult()
    object LocationDisabled : LocationResult()
    data class Success(val location: LocationModel) : LocationResult()
    data class Error(val message: String) : LocationResult()
}