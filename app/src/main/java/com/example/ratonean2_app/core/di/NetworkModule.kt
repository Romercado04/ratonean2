package com.example.ratonean2_app.core.di

import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.dsl.module


val networkModule = module {
    single {
        HttpClient(Android){
            install(ContentNegotiation){
                json(Json{
                    ignoreUnknownKeys = true
                    prettyPrint = true
                })
            }
        }
    }
}