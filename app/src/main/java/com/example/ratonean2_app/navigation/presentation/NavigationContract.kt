package com.example.ratonean2_app.navigation.presentation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object ShoppingList : Screen("shopping_list")
    object Profile : Screen("profile")
}