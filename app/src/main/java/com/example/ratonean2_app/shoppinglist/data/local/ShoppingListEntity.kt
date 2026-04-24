package com.example.ratonean2_app.shoppinglist.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "shopping_lists")
data class ShoppingListEntity(
    @PrimaryKey val listId: String = UUID.randomUUID().toString(),
    val name: String,
    val isDefault: Boolean = false, // Esta es la "Lista Activa"
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "shopping_items",
    foreignKeys = [
        ForeignKey(
            entity = ShoppingListEntity::class,
            parentColumns = ["listId"],
            childColumns = ["listId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ShoppingItemEntity(
    @PrimaryKey val itemId: String = UUID.randomUUID().toString(),
    val listId: String, // Relación con la lista
    val productId: String, // Id del producto original (de la API/Room)
    val description: String,
    val brand: String,
    val imageUrl: String?,
    val price: Double,
    val quantity: Int = 1,
    val isChecked: Boolean = false,
    val commerceId: String,
    val branchId: String
)

