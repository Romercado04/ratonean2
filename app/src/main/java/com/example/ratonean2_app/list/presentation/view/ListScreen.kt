package com.example.ratonean2_app.list.presentation.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ratonean2_app.map.presentation.state.SearchUiState
import com.example.ratonean2_app.map.presentation.viewmodel.MapViewModel
import com.example.ratonean2_app.list.presentation.components.BranchItem
import com.example.ratonean2_app.list.presentation.components.CurrentLocationHeader
import com.example.ratonean2_app.list.presentation.components.KeywordRow
import com.example.ratonean2_app.list.presentation.components.ListWithoutLocation
import com.example.ratonean2_app.list.presentation.components.ProductItem


@Composable
fun ListScreen(
    mapViewModel: MapViewModel
) {
    val searchState by mapViewModel.searchState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                top = 142.dp,
                start = 8.dp,
                end = 8.dp,
                bottom = 8.dp
            ) // 🔹 deja espacio para la SearchBar
    ) {
        when (searchState) {
            is SearchUiState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            is SearchUiState.Error -> {
                val message = (searchState as SearchUiState.Error).message
                Text(
                    text = "Error: $message",
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            is SearchUiState.Results -> {
                val results = searchState as SearchUiState.Results
                val branches = results.branches
                val products = results.products
                val location = results.location

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {

                    CurrentLocationHeader(location.name ?: "Tu corazón ♡")


                    if (products.isNotEmpty()) {

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Productos populares",
                            style = MaterialTheme.typography.titleMedium
                        )
                        val chunkedProducts = products.chunked(2)

                        LazyRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(chunkedProducts) { columnProducts ->
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    columnProducts.forEach { product ->
                                        ProductItem(product = product)
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    KeywordRow(
                        keywords = listOf("pan", "leche", "arroz", "yerba", "fideos", "azúcar")
                    ) { keyword ->
                        mapViewModel.search(keyword)
                    }

                    if (branches.isNotEmpty()) {

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Sucursales cercanas",
                            style = MaterialTheme.typography.titleMedium
                        )
                        LazyRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(branches) { branch ->
                                BranchItem(branch = branch)
                            }
                        }

                    }
                }
            }

            SearchUiState.Idle -> {
                CurrentLocationHeader("Tu corazón ♡")
                ListWithoutLocation()
            }

            SearchUiState.Empty -> Unit
        }
    }
}