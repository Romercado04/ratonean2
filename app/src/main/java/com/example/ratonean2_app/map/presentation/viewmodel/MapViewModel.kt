package com.example.ratonean2_app.map.presentation.viewmodel

import android.util.Log
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ratonean2_app.branch.domain.usecase.GetNearbyBranchesUseCase
import com.example.ratonean2_app.core.network.NetworkResponse
import com.example.ratonean2_app.map.domain.model.LocationModel
import com.example.ratonean2_app.map.domain.model.LocationResult
import com.example.ratonean2_app.map.domain.usercase.GetUserLocationUseCase
import com.example.ratonean2_app.map.presentation.state.LocationPermissionState
import com.example.ratonean2_app.map.presentation.state.MapIntent
import com.example.ratonean2_app.map.presentation.state.MapStatus
import com.example.ratonean2_app.map.presentation.state.MapViewState
import com.example.ratonean2_app.map.presentation.state.SearchStatus
import com.example.ratonean2_app.places.domain.model.PlaceResult
import com.example.ratonean2_app.places.domain.usecase.GetPlacesUseCase
import com.example.ratonean2_app.product.domain.usecase.GetPopularProductsUseCase
import com.example.ratonean2_app.product.domain.usecase.GetProductsBySearchInBranches
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.collections.emptyList

@Stable
class MapViewModel(
    private val getUserLocationUseCase: GetUserLocationUseCase,
    private val getNearbyBranchesUseCase: GetNearbyBranchesUseCase,
    private val getProductsBySearchInBranches: GetProductsBySearchInBranches,
    private val getPopularProductsUseCase: GetPopularProductsUseCase,
    private val getPlacesUseCase: GetPlacesUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(MapViewState())
    val state: StateFlow<MapViewState> = _state.asStateFlow()

    @Immutable
    private class SearchManager {
        var searchJob: Job? = null
        var updateJob: Job? = null
    }

    private val jobs = SearchManager()

    fun onIntent(intent: MapIntent) {
        when (intent) {
            is MapIntent.LoadLocation -> handleLoadLocation()
            is MapIntent.UpdateLocation -> handleUpdateLocation(intent.lat, intent.lon, intent.name)
            is MapIntent.SearchQuery -> handleSearch(intent.query)
            is MapIntent.OnPermissionResult -> handlePermissionResult(intent.granted)
        }
    }

    private fun handleLoadLocation() {
        viewModelScope.launch {
            _state.update { it.copy(mapStatus = MapStatus.Loading) }
            try {
                when (val result = getUserLocationUseCase()) {
                    is LocationResult.Success -> {
                        onIntent(MapIntent.UpdateLocation(
                            result.location.latitude,
                            result.location.longitude,
                            result.location.name
                        ))
                    }
                    is LocationResult.PermissionDenied -> {
                        _state.update { it.copy(mapStatus = MapStatus.PermissionDenied, location = null) }
                    }
                    is LocationResult.LocationDisabled -> {
                        _state.update { it.copy(mapStatus = MapStatus.LocationDisabled, location = null) }
                    }
                    is LocationResult.Error -> {
                        _state.update { it.copy(mapStatus = MapStatus.Error("Error obteniendo ubicación"), location = null) }
                    }
                }
            } catch (e: Exception) {
                _state.update { it.copy(mapStatus = MapStatus.Error(e.message ?: "Error desconocido al cargar mapa")) }
            }
        }
    }

    private fun handleUpdateLocation(lat: Double, lon: Double, name: String?, distance: Double = 5.0) {
        jobs.updateJob?.cancel()
        jobs.updateJob = viewModelScope.launch {
            val newLocation = LocationModel(name, lat, lon)
            _state.update { it.copy(location = newLocation, mapStatus = MapStatus.Loading) }

            getNearbyBranchesUseCase(lat, lon, distance).collect { response ->
                when (response) {
                    is NetworkResponse.Loading -> {
                        _state.update { it.copy(mapStatus = MapStatus.Loading) }
                    }
                    is NetworkResponse.Success -> {
                        val branches = response.data.orEmpty()
                        _state.update { it.copy(branches = branches, mapStatus = MapStatus.Success) }
                        if (branches.isNotEmpty()) {
                            loadPopularProducts(branches.map { it.branchId })
                        }
                    }
                    is NetworkResponse.Failure -> {
                        _state.update { it.copy(branches = emptyList(), mapStatus = MapStatus.Success) }
                        Log.e("MapViewModel", "Error cargando sucursales")
                    }
                }
            }
        }
    }

    private fun loadPopularProducts(branchIds: List<String>) {
        viewModelScope.launch {
            getPopularProductsUseCase(branchIds, 5).collect { response ->
                if (response is NetworkResponse.Success) {
                    val products = response.data.orEmpty()
                    _state.update { it.copy(popularProductsCache = products) }
                    Log.d("MapViewModel", "Populares cacheados: ${products.size}")
                }
            }
        }
    }

    private fun handleSearch(query: String) {
        jobs.searchJob?.cancel()

        if (query.isBlank()) {
            _state.update { it.copy(
                searchStatus = SearchStatus.Idle,
                searchProducts = it.popularProductsCache,
                filteredBranches = emptyList(),
                searchPlaces = emptyList()
            ) }
            return
        }

        jobs.searchJob = viewModelScope.launch {
            try {
                delay(500)
                _state.update { it.copy(searchStatus = SearchStatus.Loading) }

                val filteredBranches = _state.value.branches.filter { branch ->
                    val branchWords = branch.name.lowercase().split(" ")
                    val queryWords = query.lowercase().split(" ")
                    queryWords.any { q -> branchWords.any { it.contains(q) } }
                }

                var products = _state.value.popularProductsCache.filter { product ->
                    val text = "${product.description} ${product.brand}".lowercase()
                    query.lowercase() in text
                }

                var places: List<PlaceResult> = emptyList()
                val branchIds = _state.value.branches.map { it.branchId }

                if (products.isEmpty() && branchIds.isNotEmpty()) {
                    val apiResponse = getProductsBySearchInBranches(branchIds, query)
                        .first { it !is NetworkResponse.Loading }

                    if (apiResponse is NetworkResponse.Success) {
                        products = apiResponse.data.orEmpty()
                    }
                }

                if (products.isEmpty()) {
                    places = fetchPlaces(query)
                }

                _state.update {
                    it.copy(
                        filteredBranches = filteredBranches,
                        searchProducts = products,
                        searchPlaces = places,
                        searchStatus = if (products.isEmpty() && places.isEmpty() && filteredBranches.isEmpty())
                            SearchStatus.Empty else SearchStatus.Success
                    )
                }

            } catch (e: kotlinx.coroutines.CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e("MapViewModel", "Error en search(): ${e.message}")
                _state.update { it.copy(searchStatus = SearchStatus.Error(e.message ?: "Error desconocido")) }
            }
        }
    }

    private suspend fun fetchPlaces(query: String): List<PlaceResult> {
        val response: NetworkResponse<List<PlaceResult>> = try {
            getPlacesUseCase(query)
        } catch (e: Exception) {
            if (e is kotlinx.coroutines.CancellationException) throw e
            Log.e("MapViewModel", "Error en fetchPlaces: ${e.message}")
            NetworkResponse.Failure(e.message ?: "Error")
        }

        return when (response) {
            is NetworkResponse.Success -> response.data.orEmpty()
            else -> emptyList()
        }
    }



    private fun handlePermissionResult(granted: Boolean) {
        val pState = if (granted) LocationPermissionState.Granted else LocationPermissionState.Denied
        _state.update { it.copy(permissionState = pState) }
    }
}
