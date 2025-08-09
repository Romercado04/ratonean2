package com.example.ratonean2_app.map.data.provider

import android.annotation.SuppressLint
import android.content.Context
import com.example.ratonean2_app.map.domain.model.LocationModel
import com.example.ratonean2_app.map.domain.provider.LocationProvider
import com.google.android.gms.location.LocationServices
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class FusedLocationProviderImpl(
    private val context: Context
) : LocationProvider {

    @SuppressLint("MissingPermission")
    override suspend fun getCurrentLocation(): LocationModel? {
        return suspendCoroutine { continuation ->
            val client = LocationServices.getFusedLocationProviderClient(context)
            client.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    continuation.resume(
                        LocationModel(location.latitude, location.longitude)
                    )
                } else {
                    continuation.resume(null)
                }
            }
        }
    }
}
