package com.example.ratonean2_app.product.domain.model

data class Product(
    val commerceId: String,
    val flagId: String,
    val branchId: String,
    val productId: String,
    val ean: String,
    val description: String,
    val presentationQuantity: Int,
    val presentationUnit: String,
    val brand: String,
    val listPrice: Double,
    val referencePrice: Double,
    val referenceQuantity: Int,
    val referenceUnit: String,
    val promoPrice1: Double,
    val promoLegend1: String,
    val promoPrice2: Double,
    val promoLegend2: String
)

//{
//  "commerceId": "string",
//  "flagId": "string",
//  "branchId": "string",
//  "productId": "string",
//  "ean": "string",
//  "description": "string",
//  "presentationQuantity": 0,
//  "presentationUnit": "string",
//  "brand": "string",
//  "listPrice": 0,
//  "referencePrice": 0,
//  "referenceQuantity": 0,
//  "referenceUnit": "string",
//  "promoPrice1": 0,
//  "promoLegend1": "string",
//  "promoPrice2": 0,
//  "promoLegend2": "string"
//}
