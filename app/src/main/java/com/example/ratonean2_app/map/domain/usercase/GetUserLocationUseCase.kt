package com.example.ratonean2_app.map.domain.usercase

import com.example.ratonean2_app.map.domain.model.LocationModel

class GetUserLocationUseCase(
    private val locationProvider: com.example.ratonean2_app.map.domain.provider.LocationProvider
) {
    suspend operator fun invoke(): LocationModel? {
        return locationProvider.getCurrentLocation()
    }
}