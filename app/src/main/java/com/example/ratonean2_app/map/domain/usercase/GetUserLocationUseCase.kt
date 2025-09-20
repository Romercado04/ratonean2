package com.example.ratonean2_app.map.domain.usercase

import com.example.ratonean2_app.map.domain.model.LocationModel
import com.example.ratonean2_app.map.domain.model.LocationResult

class GetUserLocationUseCase(
    private val locationProvider: com.example.ratonean2_app.map.domain.provider.LocationProvider
) {
    suspend operator fun invoke(): LocationResult {
        return locationProvider.getCurrentLocation()
    }
}