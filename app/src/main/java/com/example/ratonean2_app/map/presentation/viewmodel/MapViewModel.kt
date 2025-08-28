package com.example.ratonean2_app.map.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ratonean2_app.branch.domain.model.Branch
import com.example.ratonean2_app.branch.domain.usecase.GetNearbyBranchesUseCase
import com.example.ratonean2_app.core.network.NetworkResponse
import com.example.ratonean2_app.map.domain.model.LocationModel
import com.example.ratonean2_app.map.domain.usercase.GetUserLocationUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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


class MapViewModel(
    private val getUserLocationUseCase: GetUserLocationUseCase,
    private val getNearbyBranchesUseCase: GetNearbyBranchesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<MapUiState>(MapUiState.Loading)
    val uiState: StateFlow<MapUiState> = _uiState

    init {
        loadLocationAndBranches()
    }

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
                        is NetworkResponse.Success -> _uiState.value =
                            MapUiState.Success(location, response.data ?: emptyList())
                        is NetworkResponse.Failure -> _uiState.value =
                            MapUiState.Error(response.error ?: "Error al obtener sucursales")
                    }
                }

            } catch (e: Exception) {
                _uiState.value = MapUiState.Error(
                    e.message ?: "Error desconocido al cargar mapa"
                )
            }
        }
    }
}
