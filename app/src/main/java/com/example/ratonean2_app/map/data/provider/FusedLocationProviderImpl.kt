package com.example.ratonean2_app.map.data.provider

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.core.content.ContextCompat
import com.example.ratonean2_app.map.domain.model.LocationModel
import com.example.ratonean2_app.map.domain.model.LocationResult
import com.example.ratonean2_app.map.domain.provider.LocationProvider
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class FusedLocationProviderImpl(
    private val context: Context
) : LocationProvider {

    @SuppressLint("MissingPermission")
    override suspend fun getCurrentLocation(): LocationResult = suspendCoroutine { continuation ->
        // 1️⃣ Verificar permisos
        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!hasPermission) {
            continuation.resume(LocationResult.PermissionDenied)
            return@suspendCoroutine
        }

        // 2️⃣ Verificar si el GPS/ubicación está habilitado
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        if (!isEnabled) {
            continuation.resume(LocationResult.LocationDisabled)
            return@suspendCoroutine
        }

        // 3️⃣ Obtener la ubicación
        val client = LocationServices.getFusedLocationProviderClient(context)
        client.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
            null
        ).addOnSuccessListener { location ->
            continuation.resume(
                if (location != null) {
                    LocationResult.Success(LocationModel(location.latitude, location.longitude))
                }
                else {
                    LocationResult.Error("Error al obtener la ubicación")
                }
            )
        }.addOnFailureListener {
            continuation.resume(LocationResult.Error("Error al obtener la ubicación"))
        }
    }
}
