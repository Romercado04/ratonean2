package com.example.ratonean2_app.commerce.domain.usecase

import com.example.ratonean2_app.commerce.domain.model.Commerce
import com.example.ratonean2_app.commerce.domain.provider.CommerceProvider

class GetCommercesUseCase(private val commerceProvider: CommerceProvider) {
    operator fun invoke(commerce: Commerce) = commerceProvider.createCommerce(commerce)
}