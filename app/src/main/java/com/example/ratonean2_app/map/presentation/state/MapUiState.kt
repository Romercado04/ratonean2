package com.example.ratonean2_app.map.presentation.state

import com.example.ratonean2_app.branch.domain.model.Branch
import com.example.ratonean2_app.map.domain.model.LocationModel

sealed class MapUiState {
    object Loading : MapUiState()
    data class Success(
        val location: LocationModel,
        val branches: List<Branch> = emptyList()
    ) : MapUiState()
    data class Error(val message: String) : MapUiState()
    object PermissionDenied : MapUiState()
    object LocationDisabled : MapUiState()
}