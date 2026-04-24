package com.example.ratonean2_app.shoppinglist.domain.model

data class ShoppingList(
    val id: String,
    val name: String,
    val isDefault: Boolean,
    val createdAt: Long
)

data class ShoppingItem(
    val id: String,
    val listId: String,
    val productId: String,
    val description: String,
    val brand: String,
    val imageUrl: String?,
    val price: Double,
    val quantity: Int,
    val isChecked: Boolean,
    val commerceId: String,
    val branchId: String
)

