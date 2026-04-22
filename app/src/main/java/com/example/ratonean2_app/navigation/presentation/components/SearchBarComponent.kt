package com.example.ratonean2_app.navigation.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DrawerState
import com.example.ratonean2_app.map.presentation.state.MapIntent
import com.example.ratonean2_app.map.presentation.state.MapViewState
import com.example.ratonean2_app.map.presentation.state.SearchStatus
import com.example.ratonean2_app.map.presentation.viewmodel.MapViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBarHeader(
    query: String,
    onQueryChange: (String) -> Unit,
    active: Boolean,
    onActiveChange: (Boolean) -> Unit,
    onClearClick: () -> Unit,
    drawerState: DrawerState,
    mapViewModel: MapViewModel,
    uiState: MapViewState, // Usamos el estado unificado aquí
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()

    DockedSearchBar(
        inputField = {
            SearchBarDefaults.InputField(
                query = query,
                onQueryChange = {
                    onQueryChange(it)
                    mapViewModel.onIntent(MapIntent.SearchQuery(it))
                },
                onSearch = { mapViewModel.onIntent(MapIntent.SearchQuery(query)) },
                expanded = active,
                onExpandedChange = onActiveChange,
                placeholder = { Text("Buscar...") },
                leadingIcon = {
                    IconButton(onClick = { scope.launch { drawerState.open() } }) {
                        Icon(Icons.Default.Menu, contentDescription = "Menú")
                    }
                }
            )
        },
        expanded = active,
        onExpandedChange = onActiveChange,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .shadow(4.dp, RoundedCornerShape(32.dp)),
        content = {
            if (active) {
                when (val status = uiState.searchStatus) {
                    is SearchStatus.Loading -> Text("Buscando...", modifier = Modifier.padding(16.dp))
                    is SearchStatus.Error -> Text("Error: ${status.message}", modifier = Modifier.padding(16.dp))
                    else -> {
                        if (uiState.searchPlaces.isNotEmpty()) {
                            LazyColumn {
                                items(uiState.searchPlaces) { place ->
                                    Text(
                                        text = place.displayName,
                                        modifier = Modifier
                                            .padding(12.dp)
                                            .fillMaxWidth()
                                            .clickable {
                                                mapViewModel.onIntent(MapIntent.UpdateLocation(
                                                    place.lat.toDouble(),
                                                    place.lon.toDouble(),
                                                    place.name
                                                ))
                                                onActiveChange(false)
                                                onClearClick()
                                            }
                                    )
                                }
                            }
                        } else if (uiState.searchProducts.isEmpty() && query.isNotEmpty() && status is SearchStatus.Success) {
                            Text("No se encontraron resultados", modifier = Modifier.padding(16.dp))
                        }
                    }
                }
            }
        }
    )
}