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
                .replace(Regex("\\s*\\([^)]*\\)"), "")
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

    fun formatDisplayDate(dateStr: String?): String {
        if (dateStr.isNullOrBlank()) return ""

        return try {
            val inputFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
            val date = inputFormat.parse(dateStr)
            val outputFormat = SimpleDateFormat("M월 d일 (E)", Locale.KOREAN)

            date?.let { outputFormat.format(it) } ?: ""
        } catch (e: Exception) {
            dateStr
        }
    }

    fun getRelativeMonth(baseDate: Date, monthOffset: Int): Date {
        val calendar = Calendar.getInstance()
        calendar.time = baseDate
        calendar.add(Calendar.MONTH, monthOffset)
        return calendar.time
    }

    fun getMonthRange(date: Date): Pair<String, String> {
        val calendar = Calendar.getInstance()
        calendar.time = date
        val sdf = SimpleDateFormat("yyyyMMdd", Locale.getDefault())

        // 1일 설정
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        val start = sdf.format(calendar.time)

        // 해당 월의 마지막 날 설정
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
        val end = sdf.format(calendar.time)

        return Pair(start, end)
    }

    fun formatMonth(date: Date): String {
        return SimpleDateFormat("M월", Locale.getDefault()).format(date)
    }

    fun getDayOfWeek(dateStr: String): Int {
        val date = parseApiDate(dateStr) ?: return 0
        val calendar = Calendar.getInstance()
        calendar.time = date
        // Calendar.MONDAY는 2
        val day = calendar.get(Calendar.DAY_OF_WEEK)
        return when(day) {
            Calendar.MONDAY -> 1
            Calendar.TUESDAY -> 2
            Calendar.WEDNESDAY -> 3
            Calendar.THURSDAY -> 4
            Calendar.FRIDAY -> 5
            else -> 0
        }
    }
}