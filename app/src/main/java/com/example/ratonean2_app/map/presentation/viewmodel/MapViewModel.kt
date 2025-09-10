package com.example.ratonean2_app.map.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ratonean2_app.branch.domain.model.Branch
import com.example.ratonean2_app.branch.domain.usecase.GetNearbyBranchesUseCase
import com.example.ratonean2_app.core.network.NetworkResponse
import com.example.ratonean2_app.map.domain.model.LocationModel
import com.example.ratonean2_app.map.domain.usercase.GetUserLocationUseCase
import com.example.ratonean2_app.product.domain.model.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.collections.emptyList

sealed class MapUiState {
    object Loading : MapUiState()
    data class Success(
        val location: LocationModel,
        val branches: List<Branch> = emptyList()
    ) : MapUiState()
    data class Error(val message: String) : MapUiState()
    object LocationUnavailable : MapUiState()
}

sealed class SearchUiState {
    object Idle : SearchUiState()
    object Loading : SearchUiState()
    data class Results(
        val location: LocationModel,
        val branches: List<Branch> = emptyList(),
        val products: List<Product> = emptyList()
    ) : SearchUiState()
    object Empty : SearchUiState()
    data class Error(val message: String) : SearchUiState()
}


class MapViewModel(
    private val getUserLocationUseCase: GetUserLocationUseCase,
    private val getNearbyBranchesUseCase: GetNearbyBranchesUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow<MapUiState>(MapUiState.Loading)
    val uiState: StateFlow<MapUiState> = _uiState

    private val _searchState = MutableStateFlow<SearchUiState>(SearchUiState.Idle)
    val searchState: StateFlow<SearchUiState> = _searchState
    private var manualLocation: LocationModel? = null

    fun loadLocationAndBranches(distance: Double = 5.0) {
        viewModelScope.launch {
            _uiState.value = MapUiState.Loading
            try {
                val location = getUserLocationUseCase()

                if (location == null) {
                    _uiState.value = MapUiState.LocationUnavailable
                    return@launch
                }

                getNearbyBranchesUseCase(
                    latitude = location.latitude,
                    longitude = location.longitude,
                    distance = distance
                ).collect { response ->
                    when (response) {
                        is NetworkResponse.Loading -> _uiState.value = MapUiState.Loading
                        is NetworkResponse.Success -> { _uiState.value =
                                MapUiState.Success(location, response.data ?: emptyList())
                        }
                        is NetworkResponse.Failure -> _uiState.value = MapUiState.Success(location, emptyList())
                    }
                }

            } catch (e: Exception) {
                _uiState.value = MapUiState.Error(
                    e.message ?: "Error desconocido al cargar mapa"
                )
            }
        }
    }

    fun updateLocation(lat: Double, lon: Double) {
        val newLocation = LocationModel(lat, lon)
        manualLocation = newLocation
        _uiState.value = MapUiState.Success(newLocation, emptyList())
    }

    fun search(query: String) {
        viewModelScope.launch {
            val currentUiState = _uiState.value
            if (currentUiState !is MapUiState.Success) {
                _searchState.value = SearchUiState.Error("Ubicación no disponible")
                return@launch
            }

            val location = currentUiState.location
            val allBranches = currentUiState.branches

            if (query.isBlank()) {
                _searchState.value = SearchUiState.Idle
                return@launch
            }

            _searchState.value = SearchUiState.Loading

            // Filtramos branches locales
            val matchedBranches = allBranches.filter { branch ->
                branch.name.contains(query, ignoreCase = true) ||
                        branch.location.contains(query, ignoreCase = true)
            }

            val matchedProducts = if (matchedBranches.isEmpty() && allBranches.isNotEmpty()) {
                val branchIds = allBranches.map { it.branchId }
                getProductsBySearchInBranches(branchIds, query) // adaptalo a tu usecase de productos
            } else emptyList()

            _searchState.value = if (matchedBranches.isEmpty() && matchedProducts.isEmpty()) {
                SearchUiState.Empty
            } else {
                SearchUiState.Results(
                    location = location,
                    branches = matchedBranches,
                    products = matchedProducts
                )
            }
        }
    }

    // -----------------------------
    // Reset manual location si vuelve GPS activo
    // -----------------------------
    fun resetManualLocationIfGpsAvailable() {
        viewModelScope.launch {
            val location = getUserLocationUseCase()
            if (location != null) {
                manualLocation = null
                _uiState.value = MapUiState.Success(location, emptyList())
            }
        }
    }

}
