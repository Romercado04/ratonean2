package com.example.ratonean2_app.commerce.domain.model

data class Commerce(
    val id: String,
    val flagId: String,
    val cuit: String,
    val businessName: String,
    val flagName: String,
    val flagUrl: String,
    val sepaVersion: String,
    val lastUpdate: String
)

//[
//  {
//    "id": "string",
//    "flagId": "string",
//    "cuit": "string",
//    "businessName": "string",
//    "flagName": "string",
//    "flagUrl": "string",
//    "sepaVersion": "string",
//    "lastUpdate": "2023-10-27T10:00:00Z"
//  }
//]