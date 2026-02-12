package com.dawoon.todaymeal.network.model

data class WeekRange(
    val weekLabel: String, // "1주차", "2주차" 등
    val startDate: String, // "20260209"
    val endDate: String    // "20260215"
)