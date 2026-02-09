package com.dawoon.todaymeal.network.model

data class NeisResultDto(
    val CODE: String? = null,
    val MESSAGE: String? = null
)

data class NeisHeadItemDto(
    val list_total_count: Int? = null,
    val RESULT: NeisResultDto? = null
)

fun <T> List<T>?.secondOrNull(): T? = this?.getOrNull(1)
