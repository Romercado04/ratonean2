package com.example.ratonean2_app.places.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PlaceResult(
    @SerialName("display_name")val displayName: String,
    val name: String,
    val lat: String,
    val lon: String
){
    fun latAsDouble() = lat.toDoubleOrNull() ?: 0.0
    fun lonAsDouble() = lon.toDoubleOrNull() ?: 0.0
}
