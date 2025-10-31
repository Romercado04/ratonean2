package com.example.ratonean2_app.product.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ratonean2_app.core.network.NetworkResponse
import com.example.ratonean2_app.product.domain.model.Product
import com.example.ratonean2_app.product.domain.usecase.GetAllProductsUseCase
import com.example.ratonean2_app.product.domain.usecase.GetProductsByBranchUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ProductViewModel(
    private val getAllProductsUseCase: GetAllProductsUseCase,
    private val getProductsByBranchUseCase: GetProductsByBranchUseCase
) : ViewModel() {

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products

    private val _filteredProducts = MutableStateFlow<List<Product>>(emptyList())
    val filteredProducts: StateFlow<List<Product>> = _filteredProducts

    private val _isShowingProducts = MutableStateFlow(false)
    val isShowingProducts: StateFlow<Boolean> = _isShowingProducts

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        fetchProducts()
    }

    // 🔹 Obtiene todos los productos (general)
    private fun fetchProducts() {
        viewModelScope.launch {
            getAllProductsUseCase().collect { response ->
                when (response) {
                    is NetworkResponse.Success -> {
                        _products.value = response.data ?: emptyList()
                    }

                    is NetworkResponse.Failure -> {
                        _products.value = emptyList()
                    }

                    is NetworkResponse.Loading -> {
                        // podés manejar loading global acá si querés
                    }
                }
            }
        }
    }

    // 🔹 Busca productos por texto
    fun searchProducts(query: String) {
        if (query.isBlank()) {
            _filteredProducts.value = emptyList()
            return
        }

        _filteredProducts.value = _products.value.filter {
            it.description.contains(query, ignoreCase = true)
        }
    }

    // 🔹 Obtiene productos por sucursal (para el mapa)
    fun getProductsByBranch(branchId: String) {
        viewModelScope.launch {
            _isLoading.value = true

            getProductsByBranchUseCase(branchId).collect { response ->
                when (response) {
                    is NetworkResponse.Success -> {
                        _products.value = response.data ?: emptyList()
                        _isShowingProducts.value = true
                        _isLoading.value = false
                    }

                    is NetworkResponse.Failure -> {
                        _products.value = emptyList()
                        _isLoading.value = false
                    }

                    is NetworkResponse.Loading -> {
                        _isLoading.value = true
                    }
                }
            }
        }
    }

    // 🔹 Vuelve al mapa
    fun showMap() {
        _isShowingProducts.value = false
    }
}


