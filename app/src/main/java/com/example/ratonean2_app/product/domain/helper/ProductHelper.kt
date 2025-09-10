package com.example.ratonean2_app.product.domain.helper

fun String.appendParams(params: Map<String, String?>): String {
    val filtered = params.filterValues { !it.isNullOrBlank() }
    if (filtered.isEmpty()) return this
    return this + "?" + filtered.map { "${it.key}=${it.value}" }.joinToString("&")
}
