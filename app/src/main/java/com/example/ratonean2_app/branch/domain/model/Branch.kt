package com.example.ratonean2_app.branch.domain.model
import kotlinx.serialization.Serializable

@Serializable
data class Branch(
    val branchId: String,
    val commerceId: String,
    val flagId: String,
    val name: String,
    val type: String,
    val street: String,
    val number: String,
    val latitude: Double,
    val longitude: Double,
    val observations: String?,
    val neighborhood: String,
    val postalCode: String,
    val location: String,
    val province: String,
    val mondaySchedule: String?,
    val tuesdaySchedule: String?,
    val wednesdaySchedule: String?,
    val thursdaySchedule: String?,
    val fridaySchedule: String?,
    val saturdaySchedule: String?,
    val sundaySchedule: String?
)
//[
//  {
//    "commerceId": "string",
//    "flagId": "string",
//    "branchId": "string",
//    "name": "string",
//    "type": "string",
//    "street": "string",
//    "number": "string",
//    "latitude": 0,
//    "longitude": 0,
//    "observations": "string",
//    "neighborhood": "string",
//    "postalCode": "string",
//    "location": "string",
//    "province": "string",
//    "mondaySchedule": "string",
//    "tuesdaySchedule": "string",
//    "wednesdaySchedule": "string",
//    "thursdaySchedule": "string",
//    "fridaySchedule": "string",
//    "saturdaySchedule": "string",
//    "sundaySchedule": "string"
//  }
//]