package com.example.ratonean2_app.places.domain.usecase

import com.example.ratonean2_app.core.network.NetworkResponse
import com.example.ratonean2_app.places.domain.model.PlaceResult
import com.example.ratonean2_app.places.domain.provider.PlacesProvider

class GetPlacesUseCase(private val placeRepository: PlacesProvider) {
    suspend operator fun invoke(query: String): NetworkResponse<List<PlaceResult>> = placeRepository.getPlaces(query)
}