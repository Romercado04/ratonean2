package com.example.ratonean2_app.map.domain.provider

import com.example.ratonean2_app.map.domain.model.LocationResult

interface LocationProvider {
    suspend fun getCurrentLocation(): LocationResult
}