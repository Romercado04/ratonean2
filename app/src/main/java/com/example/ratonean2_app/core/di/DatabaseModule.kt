package com.example.ratonean2_app.core.di

import androidx.room.Room
import com.example.ratonean2_app.core.data.database.AppDatabase
import org.koin.dsl.module

val databaseModule = module {

    single {
        Room.databaseBuilder(
            get(),
            AppDatabase::class.java,
            "ratonean_db"
        ).build()
    }

    single { get<AppDatabase>().commerceDao() }
    single { get<AppDatabase>().branchDao() }
    single { get<AppDatabase>().productDao() }
}