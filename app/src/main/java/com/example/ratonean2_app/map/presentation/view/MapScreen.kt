package com.example.ratonean2_app.map.presentation.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.ratonean2_app.map.presentation.component.GoogleMapView
import com.example.ratonean2_app.map.presentation.state.MapUiState
import com.example.ratonean2_app.map.presentation.viewmodel.LocationPermissionState
import com.example.ratonean2_app.map.presentation.viewmodel.MapViewModel
import com.example.ratonean2_app.navigation.presentation.components.CenteredText
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MapScreen(viewModel: MapViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    val permissionState by viewModel.permissionState.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {

        when (permissionState) {
            LocationPermissionState.Granted -> {
                // si ya cargó la ubicación, muestra el mapa
                if (uiState is MapUiState.Success) {
                    val data = uiState as MapUiState.Success
                    GoogleMapView(
                        lat = data.location.latitude,
                        lon = data.location.longitude,
                        branches = data.branches
                    )
                } else {
                    CenteredText("Cargando mapa...")
                }
            }
            LocationPermissionState.Denied -> {
                // si hay ubicación previamente seteada, mostrarla aunque no haya permiso
                val location = (uiState as? MapUiState.Success)?.location
                if (location != null) {
                    GoogleMapView(
                        lat = location.latitude,
                        lon = location.longitude,
                        branches = (uiState as MapUiState.Success).branches
                    )
                } else {
                    CenteredText("Permiso denegado y sin ubicación disponible")
                }
            }
            LocationPermissionState.NotAsked -> {
                // lanzamos la request una sola vez
                val locationPermissionState = rememberPermissionState(
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                )
                LaunchedEffect(Unit) {
                    locationPermissionState.launchPermissionRequest()
                }
                LaunchedEffect(locationPermissionState.status.isGranted) {
                    viewModel.onPermissionResult(locationPermissionState.status.isGranted)
                }
                CenteredText("Solicitando permiso de ubicación...")
            }
        }
    }
}


