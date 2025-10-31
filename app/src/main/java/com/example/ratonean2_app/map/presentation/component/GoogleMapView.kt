package com.example.ratonean2_app.map.presentation.component

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.Log
import com.example.ratonean2_app.R
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.example.ratonean2_app.branch.domain.model.Branch
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import androidx.core.graphics.scale
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptor
import androidx.core.graphics.createBitmap
import com.example.ratonean2_app.branch.domain.helper.assignImageToCommerce

@Composable
fun GoogleMapView(
    lat: Double,
    lon: Double,
    branches: List<Branch>
) {
    val context = LocalContext.current
    val userPosition = LatLng(lat, lon)

    val cameraPositionState = rememberCameraPositionState()

    val markerSize = 120 // Puedes ajustar esto

    LaunchedEffect(lat, lon) {
        val update = CameraUpdateFactory.newLatLngZoom(userPosition, 15f)
        cameraPositionState.animate(update, 1500)
    }

    GoogleMap(
        cameraPositionState = cameraPositionState,
    ) {
        // Marker del usuario
        val userMarkerIcon = remember {
            drawableToBitmapDescriptor(
                context,
                R.drawable.markers_ratonean2,
                markerSize,
                markerSize
            )
        }

        Marker(
            state = MarkerState(position = userPosition),
            title = "Tu ubicación",
            icon = userMarkerIcon
        )

        // Markers de branches
        branches.forEach { branch ->

            val commerceIdString = branch.commerceId.toString()
            val drawableResId = assignImageToCommerce(commerceIdString)

            // Icono de la sucursal: Usa la nueva función de conversión segura.
            val icon = remember(drawableResId) {
                drawableToBitmapDescriptor(
                    context,
                    drawableResId,
                    markerSize,
                    markerSize
                )
            }

            Marker(
                state = MarkerState(position = LatLng(branch.latitude, branch.longitude)),
                title = branch.name,
                icon = icon
            )
        }
    }
}

fun drawableToBitmapDescriptor(context: Context, drawableResId: Int, width: Int, height: Int): BitmapDescriptor {
    val drawable: Drawable? = ContextCompat.getDrawable(context, drawableResId)

    if (drawable == null) {
        // Fallback si el recurso no existe
        Log.e("MarkerLoader", "Drawable resource not found: $drawableResId")
        return BitmapDescriptorFactory.defaultMarker()
    }

    // Convertir el Drawable a Bitmap
    val bitmap = createBitmap(width, height)
    val canvas = Canvas(bitmap)

    drawable.setBounds(0, 0, width, height)
    drawable.draw(canvas)

    // Devolver el BitmapDescriptor
    return BitmapDescriptorFactory.fromBitmap(bitmap)
}


