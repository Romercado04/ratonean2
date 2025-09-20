package com.example.ratonean2_app.map.presentation.view

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.ratonean2_app.map.presentation.component.GoogleMapView
import com.example.ratonean2_app.map.presentation.state.MapUiState
import com.example.ratonean2_app.map.presentation.viewmodel.MapViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MapScreen(viewModel: MapViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    val locationPermissionState = rememberPermissionState(
        android.Manifest.permission.ACCESS_FINE_LOCATION
    )

    LaunchedEffect(key1 = Unit) {
        if (!locationPermissionState.status.isGranted) {
            locationPermissionState.launchPermissionRequest()
        }
    }

    LaunchedEffect(locationPermissionState.status.isGranted) {
        if (locationPermissionState.status.isGranted) {
            viewModel.loadLocationAndBranches()
        }
        else {
            viewModel.onPermissionDenied()
        }
    }

    when (uiState) {
        is MapUiState.Loading -> Text("Cargando mapa...")
        is MapUiState.LocationDisabled ->
            Text("Location disable.")
        is MapUiState.PermissionDenied ->
            Text("Permiso denegado.")
        is MapUiState.Error -> Text("Error: ${(uiState as MapUiState.Error).message}")
        is MapUiState.Success -> {
            val data = uiState as MapUiState.Success
            GoogleMapView(
                lat = data.location.latitude,
                lon = data.location.longitude,
                branches = data.branches
            )
        }
    }
}


