package com.example.ratonean2_app.map.presentation.component

import android.graphics.BitmapFactory
import com.example.ratonean2_app.R
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.example.ratonean2_app.branch.domain.model.Branch
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import androidx.core.graphics.scale
import com.google.android.gms.maps.model.BitmapDescriptor

@Composable
fun GoogleMapView(
    lat: Double,
    lon: Double,
    branches: List<Branch>
) {
    val context = LocalContext.current
    val userPosition = LatLng(lat, lon)

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(userPosition, 15f)
    }

    // Cargar y escalar icono personalizado
    val customIconState = remember { mutableStateOf<BitmapDescriptor?>(null) }


    GoogleMap(
        cameraPositionState = cameraPositionState,
        onMapLoaded = {
                // Se crea el BitmapDescriptor una vez que el mapa está listo
                val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.markers_ratonean2)
                val scaledBitmap = bitmap.scale(100, 100, false)
                customIconState.value = BitmapDescriptorFactory.fromBitmap(scaledBitmap)
        }
    ) {
        // Marker del usuario
        Marker(
            state = MarkerState(position = userPosition),
            title = "Tu ubicación"
        )

        // Markers de branches
        branches.forEach { branch ->
            Marker(
                state = MarkerState(position = LatLng(branch.latitude, branch.longitude)),
                title = branch.name,
                icon = customIconState.value ?: BitmapDescriptorFactory.defaultMarker() // fallback mientras no se cargue

            )
        }
    }
}


