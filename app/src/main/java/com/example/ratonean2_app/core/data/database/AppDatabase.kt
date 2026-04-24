package com.example.ratonean2_app.core.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.ratonean2_app.branch.data.dao.BranchDao
import com.example.ratonean2_app.branch.data.entity.BranchEntity
import com.example.ratonean2_app.commerce.data.dao.CommerceDao
import com.example.ratonean2_app.commerce.data.entity.CommerceEntity
import com.example.ratonean2_app.product.data.dao.ProductDao
import com.example.ratonean2_app.product.data.entity.ProductEntity
import com.example.ratonean2_app.shoppinglist.data.local.ShoppingItemEntity
import com.example.ratonean2_app.shoppinglist.data.local.ShoppingListDao
import com.example.ratonean2_app.shoppinglist.data.local.ShoppingListEntity

@Database(
    entities = [
        CommerceEntity::class,
        BranchEntity::class,
        ProductEntity::class,
        ShoppingListEntity::class,
        ShoppingItemEntity::class
    ],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun commerceDao(): CommerceDao
    abstract fun branchDao(): BranchDao
    abstract fun productDao(): ProductDao
    abstract fun shoppingListDao(): ShoppingListDao

}