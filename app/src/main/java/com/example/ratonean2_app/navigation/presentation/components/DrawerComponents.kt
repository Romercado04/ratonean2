package com.example.ratonean2_app.navigation.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ratonean2_app.navigation.presentation.Screen


@Composable
fun DrawerContent(onDestinationClicked: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(280.dp)
            .background(MaterialTheme.colorScheme.surface) // Usamos surface para que destaque
            .padding(24.dp)
            .statusBarsPadding()
    ) {
        Text(
            text = "Ratonean2",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(24.dp))
        HorizontalDivider(modifier = Modifier.padding(bottom = 16.dp))

        DrawerItem("Inicio", Screen.Home.route, onDestinationClicked)
        DrawerItem("Mi Perfil", Screen.Profile.route, onDestinationClicked)
        DrawerItem("Lista de Compras", Screen.ShoppingList.route, onDestinationClicked)
    }
}

@Composable
fun DrawerItem(label: String, route: String, onClick: (String) -> Unit) {
    Text(
        text = label,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(route) } // Mandamos la ruta real (ej: "shopping_list")
            .padding(vertical = 12.dp, horizontal = 8.dp),
        style = MaterialTheme.typography.titleMedium
    )
}
