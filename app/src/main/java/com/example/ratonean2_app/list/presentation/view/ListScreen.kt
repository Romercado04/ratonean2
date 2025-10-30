package com.example.ratonean2_app.list.presentation.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.ratonean2_app.map.presentation.state.MapUiState
import com.example.ratonean2_app.map.presentation.state.SearchUiState
import com.example.ratonean2_app.map.presentation.viewmodel.MapViewModel
import com.example.ratonean2_app.product.domain.model.Product
import com.example.ratonean2_app.branch.domain.model.Branch

@Composable
fun ListScreen(
    mapViewModel: MapViewModel
) {
    val searchState by mapViewModel.searchState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
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
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                ) {
                    if (products.isNotEmpty()) {
                        Text("Productos populares")
                        LazyRow(modifier = Modifier.fillMaxSize()) {
                            items(products) { product ->
                                ProductItem(product = product)
                            }
                        }
                    } else {
                        Text(
                            text = "No hay productos populares :(",
                        )
                    }
                    if (branches.isNotEmpty()) {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(branches) { branch ->
                                BranchItem(branch = branch)
                            }
                        }
                    } else {
                        Text(
                            text = "No hay sucursales cercanas",
                        )
                    }
                }
            }

            SearchUiState.Idle -> {
                //nones
            }

            SearchUiState.Empty -> {
                //none
            }
        }
    }
}

@Composable
private fun ProductItem(product: Product) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
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
            Column {
                Text(product.description, style = MaterialTheme.typography.titleMedium)
                product.brand?.let {
                    Text(
                        it,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun BranchItem(branch: Branch) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(branch.name, style = MaterialTheme.typography.titleMedium)
        }
    }
}
