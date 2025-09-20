package com.example.ratonean2_app.map.presentation.state

import com.example.ratonean2_app.branch.domain.model.Branch
import com.example.ratonean2_app.map.domain.model.LocationModel
import com.example.ratonean2_app.places.domain.model.PlaceResult
import com.example.ratonean2_app.product.domain.model.Product

sealed class SearchUiState {
    object Idle : SearchUiState()
    object Loading : SearchUiState()
    data class Results(
        val location: LocationModel,
        val branches: List<Branch> = emptyList(),
        val products: List<Product> = emptyList(),
        val places: List<PlaceResult> = emptyList()
    ) : SearchUiState()
    object Empty : SearchUiState()
    data class Error(val message: String) : SearchUiState()
}
