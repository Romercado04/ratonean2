package com.example.ratonean2_app.map.presentation.view

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.ratonean2_app.map.presentation.component.GoogleMapView
import com.example.ratonean2_app.map.presentation.viewmodel.MapUiState
import com.example.ratonean2_app.map.presentation.viewmodel.MapViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import org.koin.androidx.compose.koinViewModel

@Composable
fun MapScreen(viewModel: MapViewModel = koinViewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    when (uiState) {
        is MapUiState.Loading -> Text("Cargando mapa...")
        is MapUiState.LocationUnavailable -> Text("No pudimos obtener tu ubicación")
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


