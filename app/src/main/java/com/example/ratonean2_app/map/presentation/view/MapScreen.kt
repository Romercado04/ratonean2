package com.example.ratonean2_app.map.presentation.view

import androidx.compose.animation.Crossfade
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ratonean2_app.map.presentation.component.GoogleMapView
import com.example.ratonean2_app.map.presentation.state.MapUiState
import com.example.ratonean2_app.map.presentation.viewmodel.MapViewModel
import com.example.ratonean2_app.product.presentation.components.ProductsOfBranch
import com.example.ratonean2_app.product.presentation.viewmodel.ProductViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MapScreen(
    mapViewModel: MapViewModel,
    productViewModel: ProductViewModel
) {
    val uiState by mapViewModel.uiState.collectAsState()
    val isShowingProducts by productViewModel.isShowingProducts.collectAsState()
    val products by productViewModel.products.collectAsState()
    val isLoading by productViewModel.isLoading.collectAsState()

    val locationPermissionState = rememberPermissionState(
        android.Manifest.permission.ACCESS_FINE_LOCATION
    )

    //permissions
    LaunchedEffect(Unit) {
        if (!locationPermissionState.status.isGranted) {
            locationPermissionState.launchPermissionRequest()
        }
    }

    // load ubication and branches
    LaunchedEffect(locationPermissionState.status.isGranted) {
        if (locationPermissionState.status.isGranted) {
            mapViewModel.loadLocationAndBranches()
        } else {
            mapViewModel.onPermissionDenied()
        }
    }

    when (uiState) {
        is MapUiState.Loading -> Text("Cargando mapa...")
        is MapUiState.LocationDisabled -> Text("Ubicación deshabilitada.")
        is MapUiState.PermissionDenied -> Text("Permiso denegado.")
        is MapUiState.Error -> Text("Error: ${(uiState as MapUiState.Error).message}")
        is MapUiState.Success -> {
            val data = uiState as MapUiState.Success

            Crossfade(targetState = isShowingProducts) { showingProducts ->
                if (showingProducts) {
                    // list of products by branch
                    ProductsOfBranch(
                        products = products,
                        isLoading = isLoading,
                        onBack = { productViewModel.showMap() }
                    )
                } else {
                    // show map with markers
                    GoogleMapView(
                        lat = data.location.latitude,
                        lon = data.location.longitude,
                        branches = data.branches,
                        onMarkerClick = { branch ->
                            productViewModel.getProductsByBranch(branch.branchId)
                        }
                    )
                }
            }
        }
    }
}



