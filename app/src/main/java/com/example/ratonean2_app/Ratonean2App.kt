package com.example.ratonean2_app

import android.app.Application
import com.example.ratonean2_app.map.di.locationModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class Ratonean2App : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@Ratonean2App)
            modules(
                listOf(
                    locationModule
                )
            )
        }
    }
}