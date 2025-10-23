package com.example.ratonean2_app.product.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ratonean2_app.core.network.NetworkResponse
import com.example.ratonean2_app.product.domain.model.Product
import com.example.ratonean2_app.product.domain.usecase.GetAllProductsUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ProductViewModel(
    private val getAllProductsUseCase: GetAllProductsUseCase
) : ViewModel() {

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products

    private val _filteredProducts = MutableStateFlow<List<Product>>(emptyList())
    val filteredProducts: StateFlow<List<Product>> = _filteredProducts

    init {
        fetchProducts()
    }

    private fun fetchProducts() {
        viewModelScope.launch {
            getAllProductsUseCase().collect { response ->
                when (response) {
                    is NetworkResponse.Success -> {
                        _products.value = response.data ?: emptyList()
                    }
                    is NetworkResponse.Failure -> {
                        // podés manejar el error acá si querés
                        _products.value = emptyList()
                    }

                    is NetworkResponse.Loading<*> -> TODO()
                }
            }
        }
    }

    fun searchProducts(query: String) {
        if (query.isBlank()) {
            _filteredProducts.value = emptyList()
            return
        }

        _filteredProducts.value = _products.value.filter {
            it.description.contains(query, ignoreCase = true)
        }
    }
}

