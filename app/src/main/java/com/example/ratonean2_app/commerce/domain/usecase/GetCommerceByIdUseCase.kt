package com.example.ratonean2_app.commerce.domain.usecase

import com.example.ratonean2_app.commerce.domain.provider.CommerceProvider

class GetCommerceByIdUseCase(private val commerceProvider: CommerceProvider) {
    operator fun invoke(id: String) = commerceProvider.getCommerceById(id)
}