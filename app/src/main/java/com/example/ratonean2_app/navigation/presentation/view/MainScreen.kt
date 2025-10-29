package com.example.ratonean2_app.navigation.presentation.view

import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ratonean2_app.R
import com.example.ratonean2_app.map.presentation.state.SearchUiState
import com.example.ratonean2_app.map.presentation.view.MapScreen
import com.example.ratonean2_app.map.presentation.viewmodel.MapViewModel
import com.example.ratonean2_app.navigation.presentation.components.DrawerContent
import com.example.ratonean2_app.product.presentation.components.ProductCard
import com.example.ratonean2_app.product.presentation.viewmodel.ProductViewModel
import com.example.ratonean2_app.profile.ProfileScreen
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    mapViewModel: MapViewModel = koinViewModel(),
     viewModel: ProductViewModel = koinViewModel()
) {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var query by remember { mutableStateOf("") }
    var active by remember { mutableStateOf(false) }

    val searchState by mapViewModel.searchState.collectAsState()
    var currentIndex by remember { mutableIntStateOf(0) } // índice actual del carrusel

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
            // 🌍 Mapa al fondo
            NavHost(
                navController = navController,
                startDestination = "home"
            ) {
                composable("home") { MapScreen(viewModel = mapViewModel) }
                composable("profile") { ProfileScreen() }
                composable("settings") { SettingsScreen() }
            }

            // 🔍 Barra de búsqueda flotante
            val onActiveChange: (Boolean) -> Unit = { isActive ->
                active = isActive
                if (isActive) mapViewModel.search("")
            }
            val colors1 = SearchBarDefaults.colors()

            DockedSearchBar(
                inputField = {
                    SearchBarDefaults.InputField(
                        query = query,
                        onQueryChange = {
                            query = it
                            mapViewModel.search(it)
                            currentIndex = 0 // Reiniciar índice al buscar
                        },
                        onSearch = { mapViewModel.search(query) },
                        expanded = active,
                        onExpandedChange = onActiveChange,
                        placeholder = { Text("Buscar...") },
                        leadingIcon = {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(Icons.Default.Menu, contentDescription = "Menú")
                            }
                        },
                        colors = colors1.inputFieldColors,
                    )
                },
                expanded = active,
                onExpandedChange = onActiveChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .align(Alignment.TopCenter)
                    .fillMaxWidth(0.9f),
                shape = RoundedCornerShape(32.dp),
                colors = colors1,
                tonalElevation = 6.dp,
                shadowElevation = SearchBarDefaults.ShadowElevation,
                content = {
                    // 🔸 Mientras está expandido, mostrás sugerencias o lugares
                    if (active) {
                        when (searchState) {
                            is SearchUiState.Loading -> Text("Buscando...")
                            is SearchUiState.Empty -> Text("Vacio")
                            is SearchUiState.Error -> Text("Error buscando")
                            is SearchUiState.Results -> {
                                val results = searchState as SearchUiState.Results
                                LazyColumn {
                                    if (results.places.isNotEmpty()) {
                                        items(results.places) { place ->
                                            Text(
                                                text = place.displayName,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(16.dp)
                                                    .clickable {
                                                        mapViewModel.updateLocation(
                                                            place.lat.toDouble(),
                                                            place.lon.toDouble()
                                                        )
                                                        active = false
                                                    }
                                            )
                                        }
                                    } else {
                                        item {
                                            Text(
                                                "No hay lugares disponibles",
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(16.dp)
                                            )
                                        }
                                    }
                                }
                            }
                            else -> {}
                        }
                    }
                },
            )

            // 🧩 Carrusel sobre el mapa (solo si hay resultados)
            if (searchState is SearchUiState.Results) {
                val results = searchState as SearchUiState.Results
                val products = results.products

                if (products.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 48.dp)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        // Flecha izquierda
                        if (products.size > 1) {
                            IconButton(
                                onClick = {
                                    currentIndex =
                                        if (currentIndex > 0) currentIndex - 1 else products.lastIndex
                                },
                                modifier = Modifier
                                    .align(Alignment.CenterStart)
                                    .padding(start = 16.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ArrowBack,
                                    contentDescription = "Anterior",
                                    tint = Color.Black
                                )
                            }
                        }

                        // Tarjeta actual del carrusel
                        val product = products[currentIndex]
                        ProductCard(
                            storeLogoRes = R.drawable.markers_ratonean2,
                            productImageUrl = product.imageUrl ?: "",
                            productName = product.description,
                            productPrice = "$${product.listPrice}",
                            modifier = Modifier
                                .width(280.dp)
                                .shadow(8.dp, RoundedCornerShape(16.dp))
                        )

                        // Flecha derecha
                        if (products.size > 1) {
                            IconButton(
                                onClick = {
                                    currentIndex =
                                        if (currentIndex < products.lastIndex) currentIndex + 1 else 0
                                },
                                modifier = Modifier
                                    .align(Alignment.CenterEnd)
                                    .padding(end = 16.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ArrowForward,
                                    contentDescription = "Siguiente",
                                    tint = Color.Black
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// DE TESTING
@Composable
fun SettingsScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Pantalla Configuración")
    }
}