package com.example.ratonean2_app.navigation.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DrawerContent(onDestinationClicked: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(280.dp) // ancho fijo recomendado
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Text("Menú", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(24.dp))
        DrawerItem("Home", onDestinationClicked)
        DrawerItem("Profile", onDestinationClicked)
        DrawerItem("Settings", onDestinationClicked)
    }
}
@Composable
fun DrawerItem(title: String, onClick: (String) -> Unit) {
    Text(
        text = title,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick(title.lowercase()) },
        style = MaterialTheme.typography.bodyLarge
    )
}
