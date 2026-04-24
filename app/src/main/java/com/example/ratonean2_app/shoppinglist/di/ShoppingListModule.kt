package com.example.ratonean2_app.shoppinglist.di

import com.example.ratonean2_app.core.data.database.AppDatabase
import com.example.ratonean2_app.shoppinglist.data.local.ShoppingListLocalDataSource
import com.example.ratonean2_app.shoppinglist.data.repository.ShoppingListRepositoryImpl
import com.example.ratonean2_app.shoppinglist.domain.repository.ShoppingListRepository
import com.example.ratonean2_app.shoppinglist.domain.usecases.AddToActiveListUseCase
import com.example.ratonean2_app.shoppinglist.domain.usecases.GetAllShoppingListsUseCase
import com.example.ratonean2_app.shoppinglist.domain.usecases.GetItemsFromListUseCase
import com.example.ratonean2_app.shoppinglist.domain.usecases.ManageShoppingItemUseCase
import com.example.ratonean2_app.shoppinglist.domain.usecases.ManageShoppingListUseCase
import com.example.ratonean2_app.shoppinglist.domain.usecases.SetActiveListUseCase
import org.koin.dsl.module

val shoppingListModule = module {
    single { get<AppDatabase>().shoppingListDao() }
    single { ShoppingListLocalDataSource(get()) }
    single<ShoppingListRepository> { ShoppingListRepositoryImpl(get()) }

    factory { GetAllShoppingListsUseCase(get()) }
    factory { GetItemsFromListUseCase(get()) }
    factory { AddToActiveListUseCase(get()) }
    factory { SetActiveListUseCase(get()) }
    factory { ManageShoppingItemUseCase(get()) }
    factory { ManageShoppingListUseCase(get()) }
}