package com.example.ratonean2_app.places.domain.provider

import com.example.ratonean2_app.core.network.NetworkResponse
import com.example.ratonean2_app.places.domain.model.PlaceResult
import kotlinx.coroutines.flow.Flow

interface PlacesProvider {
    suspend fun getPlaces(query: String): Flow<NetworkResponse<List<PlaceResult>>>
}