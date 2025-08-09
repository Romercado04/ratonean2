package com.example.ratonean2_app.map.presentation.view

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.ratonean2_app.map.presentation.component.GoogleMapView
import com.example.ratonean2_app.map.presentation.viewmodel.MapViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MapScreen(viewModel: MapViewModel = koinViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val locationPermission = rememberPermissionState(
        android.Manifest.permission.ACCESS_FINE_LOCATION
    )

    LaunchedEffect(locationPermission.status.isGranted) {
        if (locationPermission.status.isGranted) {
            viewModel.loadLocation()
        } else {
            locationPermission.launchPermissionRequest()
        }
    }

    when {
        locationPermission.status.isGranted -> {
            uiState.location?.let {
                GoogleMapView(lat = it.latitude, lon = it.longitude)
            } ?: Text("Obteniendo ubicación...")
        }
        locationPermission.status.shouldShowRationale -> {
            Text("Necesitamos tu ubicación para mostrar el mapa")
        }
        else -> {
            Text("Permiso denegado")
        }
    }
}

