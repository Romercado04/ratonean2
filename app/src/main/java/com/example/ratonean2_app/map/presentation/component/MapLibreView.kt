package com.example.ratonean2_app.map.presentation.component

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun MapLibreView(
    lat: Double,
    lon: Double
) {
    val context = LocalContext.current

    val mapView = remember {
        org.maplibre.android.maps.MapView(context).apply {
            onCreate(null)
        }
    }

    var mapLibreMap by remember { mutableStateOf<org.maplibre.android.maps.MapLibreMap?>(null) }

    AndroidView(
        factory = { mapView },
        modifier = Modifier.fillMaxSize()
    ) { view ->
        view.getMapAsync { map ->
            mapLibreMap = map

            map.setStyle("https://tiles.openfreemap.org/styles/liberty") {
                android.util.Log.d("MAP", "STYLE LOADED OK")
            }
        }
    }

    LaunchedEffect(lat, lon, mapLibreMap) {
        mapLibreMap?.let { map ->
            val position = org.maplibre.android.geometry.LatLng(lat, lon)

            map.animateCamera(
                org.maplibre.android.camera.CameraUpdateFactory.newLatLngZoom(
                    position,
                    13.0
                ),
                3000
            )
        }
    }
}