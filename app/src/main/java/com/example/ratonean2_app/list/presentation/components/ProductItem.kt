package com.example.ratonean2_app.list.presentation.components

import android.R.attr.contentDescription
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.ratonean2_app.branch.domain.helper.assignImageToCommerce
import com.example.ratonean2_app.product.domain.model.Product

@Composable
fun ProductItem(product: Product) {
    Card(
        modifier = Modifier
            .height(130.dp) // un poco más alto para las 3 filas
            .widthIn(min = 130.dp),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            // 1️⃣ Fila: Producto + Precio
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                product.imageUrl?.let {
                    AsyncImage(
                        model = it,
                        contentDescription = product.description,
                        modifier = Modifier
                            .size(60.dp)
                            .padding(end = 12.dp)
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                product.listPrice?.let { price ->
                    Text(
                        text = "$${price}",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // 2️⃣ Fila: Descripción
            Text(
                text = product.description,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            // 3️⃣ Fila: Icono de branch
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                product.commerceId?.let { commerceId ->
                    AsyncImage(
                        model = assignImageToCommerce(commerceId),
                        contentDescription = "Icono de la sucursal",
                        modifier = Modifier
                            .size(28.dp)
                    )
                }
            }
        }
    }
}
