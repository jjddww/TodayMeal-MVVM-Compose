package com.dawoon.todaymeal.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

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


object DateCalculator {
    private val apiFormatter = SimpleDateFormat("yyyyMMdd", Locale.KOREAN)
    fun parseApiDate(dateStr: String): Date? {
        return try {
            apiFormatter.parse(dateStr)
        } catch (e: Exception) {
            null
        }
    }

    fun getDateRange(baseDateStr: String, days: Int): Pair<String, String> {
        val calendar = Calendar.getInstance()
        val baseDate = parseApiDate(baseDateStr) ?: Date() // 파싱 실패 시 오늘 기준

        calendar.time = baseDate

        calendar.add(Calendar.DAY_OF_YEAR, -days)
        val startDate = apiFormatter.format(calendar.time)

        calendar.time = baseDate
        calendar.add(Calendar.DAY_OF_YEAR, days)
        val endDate = apiFormatter.format(calendar.time)

        return Pair(startDate, endDate)
    }

    fun formatToDisplay(date: Date): String {
        val formatter = SimpleDateFormat("M월 d일 (E)", Locale.KOREAN)
        return formatter.format(date)
    }

    fun formatForApi(date: Date): String {
        val formatter = SimpleDateFormat("yyyyMMdd", Locale.KOREAN)
        return formatter.format(date)
    }

    fun getAllDatesInRange(startDateStr: String, endDateStr: String): List<String> {
        val dates = mutableListOf<String>()
        val calendar = Calendar.getInstance()

        val start = parseApiDate(startDateStr) ?: return emptyList()
        val end = parseApiDate(endDateStr) ?: return emptyList()

        calendar.time = start
        while (!calendar.time.after(end)) {
            dates.add(formatForApi(calendar.time))
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }
        return dates
    }
}