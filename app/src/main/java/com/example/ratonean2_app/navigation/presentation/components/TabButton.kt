package com.example.ratonean2_app.navigation.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun TabButton(
    text: String,
    selectedTab: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = if (selectedTab) FontWeight.Bold else FontWeight.Normal,
                color = if (selectedTab) Color.Black else Color.LightGray
            )
        )

        Spacer(modifier = Modifier.height(4.dp))

        if (selectedTab) {
            Box(
                modifier = Modifier
                    .height(4.dp)
                    .width(16.dp)
                    .background(Color.Black, shape = RoundedCornerShape(12.dp))
            )
        } else {
            Spacer(modifier = Modifier.height(6.dp)) // mantiene la alineación
        }
    }
}
