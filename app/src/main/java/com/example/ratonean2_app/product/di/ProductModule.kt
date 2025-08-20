package com.example.ratonean2_app.product.di

import com.example.ratonean2_app.product.data.provider.ProductProviderImpl
import com.example.ratonean2_app.product.domain.provider.ProductProvider
import com.example.ratonean2_app.product.domain.usecase.CreateNewProductUseCase
import com.example.ratonean2_app.product.domain.usecase.DeleteProductUseCase
import com.example.ratonean2_app.product.domain.usecase.GetAllProductsUseCase
import com.example.ratonean2_app.product.domain.usecase.GetProductByIdUseCase
import com.example.ratonean2_app.product.domain.usecase.UpdateProductUseCase
import org.koin.dsl.module

val productModule = module {
    single<ProductProvider> { ProductProviderImpl(get()) }
    factory { CreateNewProductUseCase(get()) }
    factory { GetAllProductsUseCase(get()) }
    factory { GetProductByIdUseCase(get()) }
    factory { UpdateProductUseCase(get()) }
    factory { DeleteProductUseCase(get()) }
}