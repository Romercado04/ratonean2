package com.example.ratonean2_app.map.presentation.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ratonean2_app.map.presentation.component.GoogleMapView
import com.example.ratonean2_app.map.presentation.component.MapLibreView
import com.example.ratonean2_app.map.presentation.state.LocationPermissionState
import com.example.ratonean2_app.map.presentation.state.MapIntent
import com.example.ratonean2_app.map.presentation.state.MapStatus
import com.example.ratonean2_app.map.presentation.viewmodel.MapViewModel
import com.example.ratonean2_app.navigation.presentation.components.CenteredText
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MapScreen(viewModel: MapViewModel) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()

    val locationPermissionState = rememberPermissionState(
        android.Manifest.permission.ACCESS_FINE_LOCATION
    )

    LaunchedEffect(locationPermissionState.status.isGranted) {
        viewModel.onIntent(MapIntent.OnPermissionResult(locationPermissionState.status.isGranted))
    }

    LaunchedEffect(uiState.permissionState) {
        if (uiState.permissionState == LocationPermissionState.Granted) {
            viewModel.onIntent(MapIntent.LoadLocation)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when (uiState.permissionState) {
            LocationPermissionState.Granted -> {
                when (val status = uiState.mapStatus) {
                    is MapStatus.Loading -> {
                        CenteredText("Cargando mapa...")
                    }
                    is MapStatus.Success -> {
                        uiState.location?.let { loc ->
                            MapLibreView(
                                lat = loc.latitude,
                                lon = loc.longitude,
                            )
                        }
                    }
                    is MapStatus.Error -> {
                        CenteredText("Error: ${status.message}")
                    }
                    is MapStatus.PermissionDenied, MapStatus.LocationDisabled -> {
                        CenteredText("El GPS está desactivado o sin permisos.")
                    }
                }
            }

            LocationPermissionState.Denied -> {
                if (uiState.location != null) {
                    MapLibreView(
                        lat = uiState.location!!.latitude,
                        lon = uiState.location!!.longitude,
                    )
                } else {
                    CenteredText("Permiso denegado. No podemos mostrar tu ubicación actual.")
                }
            }

            LocationPermissionState.NotAsked -> {
                // Lanzamos la solicitud de permiso
                LaunchedEffect(Unit) {
                    locationPermissionState.launchPermissionRequest()
                }
                CenteredText("Solicitando permiso de ubicación...")
            }
        }
    }
}


