package com.dawoon.todaymeal.util

fun formatMealText(rawText: String): String {
    return rawText
        .split(Regex("<br\\s*/?>"))
        .map {
            it.trim()
                .replace(Regex("\\s*\\([\\d.]+\\)"), "")
                .replace(Regex("\\.$"), "")
        }
        .filter { it.isNotEmpty() }
        .joinToString(separator = "\n")
}