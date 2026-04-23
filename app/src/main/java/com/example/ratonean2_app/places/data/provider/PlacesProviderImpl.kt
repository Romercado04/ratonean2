package com.example.ratonean2_app.places.data.provider

import com.example.ratonean2_app.core.network.ApiUrls
import com.example.ratonean2_app.core.network.NetworkResponse
import com.example.ratonean2_app.places.domain.model.PlaceResult
import com.example.ratonean2_app.places.domain.provider.PlacesProvider
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.isSuccess
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class PlacesProviderImpl(private val client: HttpClient) : PlacesProvider {
    override suspend fun getPlaces(query: String): NetworkResponse<List<PlaceResult>> {
        return try {
            val response = client.get(ApiUrls.PLACES_URL) {
                parameter("q", query)
                parameter("format", "json")
                parameter("addressdetails", 1)
                parameter("limit", 5)
                parameter("countrycodes", "ar")
            }

            if (response.status.isSuccess()) {
                NetworkResponse.Success(response.body<List<PlaceResult>>())
            } else {
                NetworkResponse.Failure("Error: ${response.status.value}")
            }
        } catch (e: Exception) {
            if (e is kotlinx.coroutines.CancellationException) throw e
            NetworkResponse.Failure(e.message ?: "Unknown error")
        }
    }
}