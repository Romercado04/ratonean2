package com.example.ratonean2_app.map.presentation.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ratonean2_app.map.presentation.component.MapLibreView
import com.example.ratonean2_app.map.presentation.state.LocationPermissionState
import com.example.ratonean2_app.map.presentation.state.MapIntent
import com.example.ratonean2_app.map.presentation.state.MapStatus
import com.example.ratonean2_app.map.presentation.state.MapViewState
import com.example.ratonean2_app.map.presentation.viewmodel.MapViewModel
import com.example.ratonean2_app.navigation.presentation.components.CenteredText
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MapScreen(
    uiState: MapViewState,
    onIntent: (MapIntent) -> Unit
) {
    val locationPermissionState = rememberPermissionState(
        android.Manifest.permission.ACCESS_FINE_LOCATION
    )

    LaunchedEffect(Unit) {
        onIntent(MapIntent.OnPermissionResult(locationPermissionState.status.isGranted))
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (uiState.location != null && uiState.mapStatus is MapStatus.Success) {
            MapLibreView(
                lat = uiState.location.latitude,
                lon = uiState.location.longitude,
                branches = uiState.branches
            )
        } else {
            when (uiState.permissionState) {
                LocationPermissionState.NotAsked -> {
                    LaunchedEffect(Unit) {
                        locationPermissionState.launchPermissionRequest()
                    }
                    CenteredText("Solicitando ubicación...")
                }

                else -> {
                    when (val status = uiState.mapStatus) {
                        is MapStatus.Loading -> CenteredText("Buscando tu zona...")
                        is MapStatus.Error -> CenteredText("Error: ${status.message}")
                        is MapStatus.PermissionDenied -> {
                            CenteredText("Sin permiso de GPS. Por favor, usá el buscador para elegir tu zona.")
                        }
                        else -> {}
                    }
                }
            }
        }
    }
}