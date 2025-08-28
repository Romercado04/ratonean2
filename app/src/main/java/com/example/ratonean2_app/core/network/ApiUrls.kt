package com.example.ratonean2_app.core.network

object ApiUrls {
    const val BASE_URL =  "http://192.168.100.65:8080"
    const val LOGIN = "${BASE_URL}/auth/login"
    const val AUTH_GOOGLE = "${BASE_URL}/auth/google"

    const val USERS = "${BASE_URL}/users"
    const val USER_BY_ID = "${BASE_URL}/users/{id}"
    const val USER_BY_EMAIL = "${BASE_URL}/users/by-email/{email}"

    const val COMMERCES = "${BASE_URL}/commerce"
    const val COMMERCES_BY_ID = "${BASE_URL}/commerce/{id}"

    const val BRANCHES = "${BASE_URL}/branch"
    const val BRANCHES_BY_ID = "${BASE_URL}/branch/{id}"
    const val BRANCHES_NEARBY = "${BASE_URL}/branch/nearby"

    const val PRODUCTS = "${BASE_URL}/product"
    const val PRODUCTS_BY_ID = "${BASE_URL}/product/{id}"

}