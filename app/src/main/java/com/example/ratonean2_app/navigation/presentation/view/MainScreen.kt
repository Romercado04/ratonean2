package com.example.ratonean2_app.navigation.presentation.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ratonean2_app.R
import com.example.ratonean2_app.navigation.presentation.components.DrawerContent
import com.example.ratonean2_app.product.presentation.components.ProductCard
import com.example.ratonean2_app.product.presentation.viewmodel.ProductViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: ProductViewModel = koinViewModel()
) {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    var query by remember { mutableStateOf("") }
    var active by remember { mutableStateOf(false) }

    val products by viewModel.products.collectAsState()
    val filteredProducts by viewModel.filteredProducts.collectAsState()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(
                onDestinationClicked = { route ->
                    scope.launch { drawerState.close() }
                    navController.navigate(route) {
                        popUpTo(navController.graph.startDestinationId) { inclusive = false }
                        launchSingleTop = true
                    }
                }
            )
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            // 🔍 SearchBar superior
            SearchBar(
                query = query,
                onQueryChange = {
                    query = it
                    viewModel.searchProducts(it)
                },
                onSearch = { active = false },
                active = active,
                onActiveChange = { active = it },
                placeholder = { Text("Buscar productos...") },
                leadingIcon = {
                    IconButton(onClick = { scope.launch { drawerState.open() } }) {
                        Icon(Icons.Default.Menu, contentDescription = "Menú")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .align(Alignment.TopCenter),
                tonalElevation = 6.dp
            ) {
                // ✅ Contenido del SearchBar solo si hay búsqueda
                if (query.isNotBlank()) {
                    if (filteredProducts.isEmpty()) {
                        Text(
                            text = "No hay resultados para \"$query\"",
                            modifier = Modifier.padding(16.dp)
                        )
                    } else {
                        // 🔄 Lista horizontal tipo carrusel
                        LazyRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(filteredProducts) { product ->
                                ProductCard(
                                    storeLogoRes = R.drawable.markers_ratonean2,
                                    productImageUrl = product.productId ?: "",
                                    productName = product.description,
                                    productPrice = "$${product.listPrice}"
                                )
                            }
                        }
                    }
                }
                // Si no hay búsqueda, no mostramos nada 👇
            }

            // 🌍 Mantiene el mapa o contenido principal
            NavHost(
                navController = navController,
                startDestination = "home"
            ) {
                composable("home") { /* MapScreen() */ }
                composable("profile") { /* ProfileScreen() */ }
                composable("settings") { /* SettingsScreen() */ }
            }
        }
    }
}


// DE TESTING
@Composable
fun ProfileScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Pantalla Perfil")
    }
}

// DE TESTING
@Composable
fun SettingsScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Pantalla Configuración")
    }
}