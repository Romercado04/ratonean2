package com.example.ratonean2_app

import android.app.Application
import com.example.ratonean2_app.auth.di.authModule
import com.example.ratonean2_app.branch.di.branchModule
import com.example.ratonean2_app.commerce.di.commerceModule
import com.example.ratonean2_app.core.di.networkModule
import com.example.ratonean2_app.map.di.locationModule
import com.example.ratonean2_app.product.di.productModule
import com.example.ratonean2_app.user.di.userModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class Ratonean2App : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@Ratonean2App)
            modules(
                listOf(
                    locationModule,
                    networkModule,
                    authModule,
                    userModule,
                    productModule,
                    commerceModule,
                    branchModule,

                )
            )
        }
    }
}