package com.example.ratonean2_app.map.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ratonean2_app.map.domain.model.LocationModel
import com.example.ratonean2_app.map.domain.usercase.GetUserLocationUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class MapUiState(
    val location: LocationModel? = null
)

class MapViewModel(
    private val getUserLocationUseCase: GetUserLocationUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(MapUiState())
    val uiState: StateFlow<MapUiState> = _uiState

    init {
        loadLocation()
    }

    fun loadLocation() {
        viewModelScope.launch {
            val location = getUserLocationUseCase()
            _uiState.value = _uiState.value.copy(location = location)
        }
    }
}