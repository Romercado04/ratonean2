package com.example.ratonean2_app.product.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ratonean2_app.R

@Composable
fun ProductCard(
    storeLogoRes: Int, // Recurso del logo de la tienda
    productImageUrl: String,
    productName: String,
    productPrice: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .width(180.dp)
            .padding(8.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .background(Color.White)
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Row superior con logo y favorito
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = storeLogoRes),
                    contentDescription = "Store logo",
                    modifier = Modifier.size(40.dp)
                )
                IconButton(onClick = { /* TODO */ }) {
                    Icon(
                        imageVector = Icons.Default.FavoriteBorder,
                        contentDescription = "Favorito"
                    )
                }
            }

            // Imagen del producto
            AsyncImage(
                model = productImageUrl,
                contentDescription = productName,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .height(100.dp)
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )

            // Nombre
            Text(
                text = productName,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(top = 4.dp),
                maxLines = 2
            )

            // Precio
            Text(
                text = productPrice,
                fontSize = 13.sp,
                color = Color.Gray,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            // Botones
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Button(
                    onClick = { /* Comparar */ },
                    modifier = Modifier
                        .weight(1f)
                        .height(32.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)
                ) {
                    Text("comparar", color = Color.Black, fontSize = 12.sp)
                }
                Button(
                    onClick = { /* Comprar */ },
                    modifier = Modifier
                        .weight(1f)
                        .height(32.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)
                ) {
                    Text("comprar", color = Color.Black, fontSize = 12.sp)
                }
            }

        }
    }
}


@Preview(showBackground = true)
@Composable
fun ProductCardPreview() {
    ProductCard(
        storeLogoRes = R.drawable.markers_ratonean2, // Logo de ejemplo
        productImageUrl = "https://ar.images.search.yahoo.com/search/images;_ylt=AwrNYsWV971oHXwGhmmr9Qt.;_ylu=Y29sbwNiZjEEcG9zAzEEdnRpZAMEc2VjA3BpdnM-?p=cocacola&fr2=piv-web&type=E210AR91215G0&fr=mcafee#id=12&iurl=https%3A%2F%2Fwww.freeiconspng.com%2Fuploads%2Fbottle-coca-cola-png-transparent-2.png&action=click",
        productName = "Coca Cola Zero Lata 354ml",
        productPrice = "$1500"
    )
}

