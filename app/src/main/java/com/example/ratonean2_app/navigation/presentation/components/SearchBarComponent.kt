package com.example.ratonean2_app.navigation.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
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
import com.example.ratonean2_app.map.presentation.state.SearchUiState
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
    searchState: SearchUiState,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val colors = SearchBarDefaults.colors()

    DockedSearchBar(
        inputField = {
            SearchBarDefaults.InputField(
                query = query,
                onQueryChange = {
                    onQueryChange(it)
                    mapViewModel.search(it)
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
                colors = colors.inputFieldColors,
            )
        },
        expanded = active,
        onExpandedChange = onActiveChange,
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .shadow(4.dp, RoundedCornerShape(32.dp)),
        shape = RoundedCornerShape(32.dp),
        colors = colors,
        tonalElevation = 6.dp,
        content = {
            if (active) {
                when (searchState) {
                    is SearchUiState.Loading -> Text("Buscando...")
                    is SearchUiState.Empty -> Text("Vacio")
                    is SearchUiState.Error -> Text("Error buscando")
                    is SearchUiState.Results -> {
                        val results = searchState as SearchUiState.Results
                        if (results.places.isNotEmpty()) {
                            results.places.forEach { place ->
                                Text(
                                    text = place.displayName,
                                    modifier = Modifier
                                        .padding(12.dp)
                                        .fillMaxWidth()
                                        .clickable {
                                            mapViewModel.updateLocation(
                                                place.lat.toDouble(),
                                                place.lon.toDouble()
                                            )
                                            onActiveChange(false)
                                            onClearClick()
                                        }
                                )
                            }
                        } else {
                            Text("No hay lugares disponibles", modifier = Modifier.padding(16.dp))
                        }
                    }
                    else -> {}
                }
            }
        }
    )
}
