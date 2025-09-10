package com.example.ratonean2_app.map.data.provider

import android.annotation.SuppressLint
import android.content.Context
import com.example.ratonean2_app.map.domain.model.LocationModel
import com.example.ratonean2_app.map.domain.provider.LocationProvider
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class FusedLocationProviderImpl(
    private val context: Context
) : LocationProvider {

    @SuppressLint("MissingPermission")
    override suspend fun getCurrentLocation(): LocationModel? {
        return suspendCoroutine { continuation ->
            val client = LocationServices.getFusedLocationProviderClient(context)
            client.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                null
            ).addOnSuccessListener { location ->
                continuation.resume(
                    location?.let { LocationModel(it.latitude, it.longitude) }
                )
            }.addOnFailureListener {
                continuation.resume(null)
            }
        }
    }
}
