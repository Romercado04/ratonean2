package com.example.ratonean2_app.commerce.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.ratonean2_app.commerce.domain.model.Commerce
import kotlinx.serialization.Serializable

@Entity(tableName = "commerce")
@Serializable
data class CommerceEntity(
    @PrimaryKey val id: String,
    val flagId: String,
    val cuit: String,
    val businessName: String,
    val flagName: String,
    val flagUrl: String,
    val sepaVersion: String,
    val lastUpdate: String
)
fun CommerceEntity.toDomain(): Commerce = Commerce(
    id = id,
    flagId = flagId,
    cuit = cuit,
    businessName = businessName,
    flagName = flagName,
    flagUrl = flagUrl,
    sepaVersion = sepaVersion,
    lastUpdate = lastUpdate
)

fun Commerce.toEntity(): CommerceEntity = CommerceEntity(
    id = id,
    flagId = flagId,
    cuit = cuit,
    businessName = businessName,
    flagName = flagName,
    flagUrl = flagUrl,
    sepaVersion = sepaVersion,
    lastUpdate = lastUpdate
)