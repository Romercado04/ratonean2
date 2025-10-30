package com.example.ratonean2_app.map.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ratonean2_app.branch.domain.model.Branch
import com.example.ratonean2_app.branch.domain.usecase.GetNearbyBranchesUseCase
import com.example.ratonean2_app.core.network.NetworkResponse
import com.example.ratonean2_app.map.domain.model.LocationModel
import com.example.ratonean2_app.map.domain.model.LocationResult
import com.example.ratonean2_app.map.domain.usercase.GetUserLocationUseCase
import com.example.ratonean2_app.map.presentation.state.MapUiState
import com.example.ratonean2_app.map.presentation.state.SearchUiState
import com.example.ratonean2_app.places.domain.model.PlaceResult
import com.example.ratonean2_app.places.domain.usecase.GetPlacesUseCase
import com.example.ratonean2_app.product.domain.model.Product
import com.example.ratonean2_app.product.domain.usecase.GetPopularProductsUseCase
import com.example.ratonean2_app.product.domain.usecase.GetProductsBySearchInBranches
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlin.collections.emptyList

class MapViewModel(
    private val getUserLocationUseCase: GetUserLocationUseCase,
    private val getNearbyBranchesUseCase: GetNearbyBranchesUseCase,
    private val getProductsBySearchInBranches: GetProductsBySearchInBranches,
    private val getPopularProductsUseCase: GetPopularProductsUseCase,
    private val getPlacesUseCase: GetPlacesUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow<MapUiState>(MapUiState.Loading)
    val uiState: StateFlow<MapUiState> = _uiState

    private val _searchState = MutableStateFlow<SearchUiState>(SearchUiState.Idle)
    val searchState: StateFlow<SearchUiState> = _searchState

    private var cachedBranches: List<Branch> = emptyList()
    private var currentLocation: LocationModel? = null
    private var popularProductsCache: List<Product> = emptyList()

    private var searchJob: Job? = null
    private var updateJob: Job? = null

    private val _permissionState = MutableStateFlow<LocationPermissionState>(LocationPermissionState.NotAsked)
    val permissionState: StateFlow<LocationPermissionState> = _permissionState


    fun loadLocationAndBranches(distance: Double = 5.0) {

        viewModelScope.launch {
            _uiState.value = MapUiState.Loading
            try {
                when (val result = getUserLocationUseCase()) {
                    is LocationResult.PermissionDenied -> {
                        currentLocation = null
                        _uiState.value = MapUiState.PermissionDenied
                    }
                    is LocationResult.LocationDisabled -> {
                        currentLocation = null
                        _uiState.value = MapUiState.LocationDisabled
                    }
                    is LocationResult.Success -> {
                        currentLocation = result.location

                        getNearbyBranchesUseCase(
                            latitude = result.location.latitude,
                            longitude = result.location.longitude,
                            distance = distance
                        ).collect { response ->
                            when (response) {
                                is NetworkResponse.Loading -> _uiState.value = MapUiState.Loading
                                is NetworkResponse.Success -> {
                                    cachedBranches = response.data.orEmpty()
                                    _uiState.value = MapUiState.Success(result.location, cachedBranches)

                                    if (cachedBranches.isNotEmpty()) {
                                        val branchIds = cachedBranches.map { it.branchId }
                                        loadPopularProducts(branchIds, 10)
                                    }
                                }
                                is NetworkResponse.Failure -> {
                                    cachedBranches = emptyList()
                                    _uiState.value = MapUiState.Success(result.location, cachedBranches)
                                }
                            }
                        }
                    }
                    is LocationResult.Error -> {
                        currentLocation = null
                        _uiState.value = MapUiState.Error("Error obteniendo ubicación")
                    }
                }
            } catch (e: Exception) {
                _uiState.value = MapUiState.Error(e.message ?: "Error desconocido al cargar mapa")
            }
        }
    }

    fun updateLocation(lat: Double, lon: Double, distance: Double = 5.0) {
        // Cancelar cualquier job previo para evitar colecciones solapadas
        updateJob?.cancel()
        updateJob = viewModelScope.launch {
            val newLocation = LocationModel(lat, lon)
            currentLocation = newLocation
            _uiState.value = MapUiState.Success(newLocation, emptyList())

            getNearbyBranchesUseCase(
                latitude = newLocation.latitude,
                longitude = newLocation.longitude,
                distance = distance
            ).collect { response ->
                when (response) {
                    is NetworkResponse.Loading -> {
                        _uiState.value = MapUiState.Loading
                    }
                    is NetworkResponse.Success -> {
                        cachedBranches = response.data.orEmpty()
                        _uiState.value = MapUiState.Success(newLocation, cachedBranches)

                        if (cachedBranches.isNotEmpty()) {
                            val branchIds = cachedBranches.map { it.branchId }
                            loadPopularProducts(branchIds, 5)
                        }
                    }
                    is NetworkResponse.Failure -> {
                        cachedBranches = emptyList()
                        _uiState.value = MapUiState.Success(newLocation, cachedBranches)
                        // Opcional: mostrar un mensaje de error al usuario
                        Log.e("MapViewModel", "Error cargando sucursales del update location")
                    }
                }
            }
        }
    }
    fun loadPopularProducts(branchIds: List<String>, limit: Int? = 5) {
        viewModelScope.launch {
            getPopularProductsUseCase(branchIds, limit).collect { response ->
                if (response is NetworkResponse.Success) {
                    popularProductsCache = response.data.orEmpty()
                    Log.d("MapViewModel", "Populares cacheados: ${popularProductsCache.size}")
                    popularProductsCache.forEach {
                        Log.d("MapViewModel", "Producto popular: ${it.description} - ${it.brand}")
                    }
                    updatePopularProducts()
                }
            }
        }
    }
    fun updatePopularProducts() {
        val location = currentLocation ?: LocationModel(0.0, 0.0)
        _searchState.value = SearchUiState.Results(
            location = location,
            branches = cachedBranches,
            products = popularProductsCache,
            places = emptyList()
        )
    }

    fun search(query: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(500) // debounce

            val location = currentLocation
            val branchIdsForProducts = cachedBranches.map { it.branchId }
            Log.d("MapViewModel", "_uiState actual: ${_uiState.value}")
            Log.d("MapViewModel", "currentLocation: $currentLocation")

            if (query.isBlank() or query.isEmpty()) {
                // Query vacía → mostrar solo productos populares desde cache
                Log.d("MapViewModel", "Query vacía → mostrando productos populares cacheados")
                updatePopularProducts()
                return@launch
            }

            _searchState.value = SearchUiState.Loading

            try {
                var filteredBranches: List<Branch> = emptyList()
                var products: List<Product> = emptyList()
                var places: List<PlaceResult> = emptyList()

                when (_uiState.value) {
                    is MapUiState.Success -> {
                        // Filtrar sucursales por query
                        filteredBranches = cachedBranches.filter { branch ->
                            val branchWords = branch.name.lowercase().split(" ")
                            val queryWords = query.lowercase().split(" ")
                            queryWords.any { q -> branchWords.any { it.contains(q) } }
                        }
                        Log.d("MapViewModel", "Branches filtradas por query '${query}': ${filteredBranches.size}")

                        // Buscar en cache de productos populares
                        products = popularProductsCache.filter { product ->
                            val text = "${product.description} ${product.brand}".lowercase()
                            query.lowercase() in text
                        }
                        Log.d("MapViewModel", "Matches en cache de populares: ${products.size}")

                        getPlacesUseCase(query)
                            .catch { e ->
                                Log.e("MapViewModel", "Error obteniendo places: ${e.message}")
                                _searchState.value = SearchUiState.Error("Error buscando lugares")
                            }
                            .collect { response ->
                                when (response) {
                                    is NetworkResponse.Success -> {
                                        places = response.data.orEmpty()
                                    }
                                    is NetworkResponse.Failure -> {
                                        _searchState.value = SearchUiState.Error("Error buscando lugares")
                                    }
                                    is NetworkResponse.Loading -> {
                                        _searchState.value = SearchUiState.Loading
                                    }
                                }
                            }

                        if (products.isEmpty() && branchIdsForProducts.isNotEmpty()) {
                            Log.d("MapViewModel", "No hay matches en cache → llamando a API de búsqueda")
                            getProductsBySearchInBranches(branchIdsForProducts, query)
                                .collect { response ->
                                    when (response) {
                                        is NetworkResponse.Success -> {
                                            products = response.data.orEmpty()
                                            Log.d("MapViewModel", "Matches desde API: ${products.size}")
                                            if(products.isEmpty()) {
                                                getPlacesUseCase(query)
                                                    .catch { e ->
                                                        Log.e("MapViewModel", "Error obteniendo places: ${e.message}")
                                                        _searchState.value = SearchUiState.Error("Error buscando lugares")
                                                    }
                                                    .collect { response ->
                                                        when (response) {
                                                            is NetworkResponse.Success -> {
                                                                places = response.data.orEmpty()
                                                            }
                                                            is NetworkResponse.Failure -> {
                                                                _searchState.value = SearchUiState.Error("Error buscando lugares")
                                                            }
                                                            is NetworkResponse.Loading -> {
                                                                _searchState.value = SearchUiState.Loading
                                                            }
                                                        }
                                                    }
                                            }
                                        }
                                        is NetworkResponse.Failure -> {
                                            Log.e("MapViewModel", "Error en API: ?")
                                        }
                                        else -> {
                                            Log.d("MapViewModel", "Otro estado: $response")
                                        }
                                    }
                                }
                        }
                    }
                    else -> {
                        getPlacesUseCase(query)
                            .catch { e ->
                                Log.e("MapViewModel", "Error obteniendo places: ${e.message}")
                                _searchState.value = SearchUiState.Error("Error buscando lugares")
                            }
                            .collect { response ->
                                when (response) {
                                    is NetworkResponse.Success -> {
                                        places = response.data.orEmpty()
                                    }
                                    is NetworkResponse.Failure -> {
                                        _searchState.value = SearchUiState.Error("Error buscando lugares")
                                    }
                                    is NetworkResponse.Loading -> {
                                        _searchState.value = SearchUiState.Loading
                                    }
                                }
                            }
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
                Log.e("MapViewModel", "Error en search(): ${e.message}")
            }
        }
    }

    fun onPermissionResult(granted: Boolean) {
        _permissionState.value = if (granted) LocationPermissionState.Granted
        else LocationPermissionState.Denied
    }

}

sealed interface LocationPermissionState {
    object NotAsked : LocationPermissionState
    object Denied : LocationPermissionState
    object Granted : LocationPermissionState
}
