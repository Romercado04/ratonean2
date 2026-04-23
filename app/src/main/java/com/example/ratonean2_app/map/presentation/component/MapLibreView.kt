package com.example.ratonean2_app.map.presentation.component

import android.graphics.BitmapFactory
import android.util.Log
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
import com.example.ratonean2_app.R
import com.example.ratonean2_app.branch.domain.helper.assignImageToCommerce
import com.example.ratonean2_app.branch.domain.model.Branch
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.plugins.annotation.SymbolOptions

@Composable
fun MapLibreView(
    lat: Double,
    lon: Double,
    branches: List<Branch>
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

            map.setStyle("https://tiles.openfreemap.org/styles/liberty") { style ->

                val symbolManager = org.maplibre.android.plugins.annotation.SymbolManager(
                    mapView,
                    map,
                    style
                )

                symbolManager.iconAllowOverlap = true

                val userBitmap = BitmapFactory.decodeResource(
                    context.resources,
                    R.drawable.markers_ratonean2
                )

                style.addImage("user-icon", userBitmap)

                symbolManager.create(
                    org.maplibre.android.plugins.annotation.SymbolOptions()
                        .withLatLng(org.maplibre.android.geometry.LatLng(lat, lon))
                        .withIconImage("user-icon")
                        .withIconSize(0.2f)
                )

                branches.forEach { branch ->

                    val iconId = "branch-${branch.branchId}"

                    val bitmap = BitmapFactory.decodeResource(
                        context.resources,
                        assignImageToCommerce(branch.commerceId.toString())
                    )

                    if (bitmap != null) {
                        style.addImage(iconId, bitmap)

                        symbolManager.create(
                            SymbolOptions()
                                .withLatLng(LatLng(branch.latitude, branch.longitude))
                                .withIconImage(iconId)
                                .withIconSize(0.1f)
                        )
                    } else {
                        Log.e("MapLibre", "Bitmap null para branch ${branch.branchId}")
                    }

                    symbolManager.create(
                        org.maplibre.android.plugins.annotation.SymbolOptions()
                            .withLatLng(
                                org.maplibre.android.geometry.LatLng(
                                    branch.latitude,
                                    branch.longitude
                                )
                            )
                            .withIconImage(iconId)
                            .withIconSize(0.1f)
                    )
                }
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
                1500
            )
        }
    }
}