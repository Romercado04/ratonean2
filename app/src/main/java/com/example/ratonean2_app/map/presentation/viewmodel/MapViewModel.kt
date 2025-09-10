package com.example.ratonean2_app.map.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ratonean2_app.branch.domain.model.Branch
import com.example.ratonean2_app.branch.domain.usecase.GetNearbyBranchesUseCase
import com.example.ratonean2_app.core.network.NetworkResponse
import com.example.ratonean2_app.map.domain.model.LocationModel
import com.example.ratonean2_app.map.domain.usercase.GetUserLocationUseCase
import com.example.ratonean2_app.places.domain.model.PlaceResult
import com.example.ratonean2_app.places.domain.usecase.GetPlacesUseCase
import com.example.ratonean2_app.product.domain.model.Product
import com.example.ratonean2_app.product.domain.usecase.GetProductsBySearchInBranches
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
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
        val products: List<Product> = emptyList(),
        val places: List<PlaceResult> = emptyList()
    ) : SearchUiState()
    object Empty : SearchUiState()
    data class Error(val message: String) : SearchUiState()
}


class MapViewModel(
    private val getUserLocationUseCase: GetUserLocationUseCase,
    private val getNearbyBranchesUseCase: GetNearbyBranchesUseCase,
    private val getProductsBySearchInBranches: GetProductsBySearchInBranches,
    private val getPlacesUseCase: GetPlacesUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow<MapUiState>(MapUiState.Loading)
    val uiState: StateFlow<MapUiState> = _uiState

    private val _searchState = MutableStateFlow<SearchUiState>(SearchUiState.Idle)
    val searchState: StateFlow<SearchUiState> = _searchState

    private var cachedBranches: List<Branch> = emptyList()
    private var currentLocation: LocationModel? = null

    private var searchJob: Job? = null


    fun loadLocationAndBranches(distance: Double = 5.0) {
        viewModelScope.launch {
            _uiState.value = MapUiState.Loading
            try {
                val location = getUserLocationUseCase()

                if (location == null) {
                    _uiState.value = MapUiState.LocationUnavailable
                    return@launch
                } else currentLocation = location

                getNearbyBranchesUseCase(
                    latitude = location.latitude,
                    longitude = location.longitude,
                    distance = distance
                ).collect { response ->
                    when (response) {
                        is NetworkResponse.Loading -> _uiState.value = MapUiState.Loading
                        is NetworkResponse.Success -> { _uiState.value =
                                MapUiState.Success(location, response.data ?: emptyList())
                                cachedBranches = response.data.orEmpty()
                            Log.d("MapViewModel", "Branches cargadas: ${cachedBranches.size} sucursales")
                            cachedBranches.forEach { branch ->
                                Log.d("MapViewModel", "Sucursal: ${branch.name}, ID: ${branch.branchId}")
                            }
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

    fun updateLocation(lat: Double, lon: Double, distance: Double = 5.0){
        viewModelScope.launch {
            val newLocation = LocationModel(lat, lon)
            currentLocation = newLocation
            _uiState.value = MapUiState.Success(newLocation, emptyList())

            val response = getNearbyBranchesUseCase(
                latitude = newLocation.latitude,
                longitude = newLocation.longitude,
                distance = distance
            ).first()

            cachedBranches = if(response is NetworkResponse.Success) response.data.orEmpty() else emptyList()
            _uiState.value = MapUiState.Success(newLocation, cachedBranches)
        }
    }

    fun search(query: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(500) // Debounce para evitar llamadas excesivas

            val location = currentLocation
            if (query.isBlank()) {
                _searchState.value = SearchUiState.Idle
                return@launch
            }

            _searchState.value = SearchUiState.Loading

            try {
                var filteredBranches: List<Branch> = emptyList()
                var products: List<Product> = emptyList()
                var places: List<PlaceResult> = emptyList()

                if (location != null) {
                    filteredBranches = cachedBranches.filter { branch ->
                        val branchWords = branch.name.lowercase().split(" ")
                        val queryWords = query.lowercase().split(" ")
                        queryWords.any { q -> branchWords.any { it.contains(q) } }
                    }

                    val branchIdsForProducts = cachedBranches.map { it.branchId }
                    Log.d("MapViewModel", "Branchesids 4 products: ${branchIdsForProducts.size} sucursales")
                    branchIdsForProducts.forEach { branch ->
                        Log.d("MapViewModel BranchIdForProducts", "Sucursal: $branch")
                    }

                    if (branchIdsForProducts.isNotEmpty()) {
                        getProductsBySearchInBranches(branchIdsForProducts, query).collect {
                            response ->
                            products = response.data.orEmpty()
                        }
                    }
                } else {
                    val placesResponse = getPlacesUseCase(query).first()
                    if (placesResponse is NetworkResponse.Success) {
                        places = placesResponse.data.orEmpty()
                    }
                }
                    _searchState.value = SearchUiState.Results(
                        location = location ?: LocationModel(0.0, 0.0),
                        branches = filteredBranches,
                        products = products,
                        places = places
                    )

            } catch (e: Exception) {
                _searchState.value = SearchUiState.Error(e.message ?: "Error buscando")
            }
        }
    }
}
