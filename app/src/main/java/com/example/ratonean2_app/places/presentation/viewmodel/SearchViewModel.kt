package com.example.ratonean2_app.places.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ratonean2_app.core.network.NetworkResponse
import com.example.ratonean2_app.places.domain.model.PlaceResult
import com.example.ratonean2_app.places.domain.usecase.GetPlacesUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SearchViewModel(
    private val getPlacesUseCase: GetPlacesUseCase
): ViewModel() {
    private val _places = MutableStateFlow<NetworkResponse<List<PlaceResult>>>(NetworkResponse.Loading())
    val places: StateFlow<NetworkResponse<List<PlaceResult>>> = _places
    private var searchJob: Job? = null
    fun searchPlaces(query: String){
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(500) // ⏳ debounce de 0.5s
            if (query.isNotBlank()) {
                getPlacesUseCase(query).collect {
                    _places.value = it
                }
            }
        }
    }
}