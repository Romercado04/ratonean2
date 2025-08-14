package com.example.ratonean2_app.auth.di

import com.example.ratonean2_app.auth.data.provider.AuthProviderImpl
import com.example.ratonean2_app.auth.domain.provider.AuthProvider
import com.example.ratonean2_app.auth.presentation.viemwodel.LoginViewModel
import com.example.ratonean2_app.auth.presentation.viemwodel.RegisterViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val authModule = module {
    single<AuthProvider> { AuthProviderImpl(get()) }
    viewModel { LoginViewModel(get()) }
    viewModel { RegisterViewModel(get()) }
}