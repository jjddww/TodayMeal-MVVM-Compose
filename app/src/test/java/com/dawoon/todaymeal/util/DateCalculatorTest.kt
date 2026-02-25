package com.dawoon.todaymeal.util

import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Calendar
import java.util.Date

class DateCalculatorTest {

    @Test
    fun `parseApiDate and formatForApi should be consistent`() {
        val dateStr = "20250514"
        val parsedDate = DateCalculator.parseApiDate(dateStr)

        assert(parsedDate != null)

        val formatted = DateCalculator.formatForApi(parsedDate!!)
        assertEquals(dateStr, formatted)
    }


    @Test
    fun `parseApiDate with invalid format should return null`() {
        assertEquals(null, DateCalculator.parseApiDate("abc"))
        assertEquals(null, DateCalculator.parseApiDate("202505"))
        assertEquals(null, DateCalculator.parseApiDate("2025-05-14"))
    }

    @Test
    fun `formatDisplayDate should return correct Korean format`() {
        val dateStr = "20250514" // 수요일
        val result = DateCalculator.formatDisplayDate(dateStr)
        assertEquals("5월 14일 (수)", result)
    }

    @Test
    fun `formatDisplayDate - invalid format should return original string`() {
        // try-catch에서 에러가 나면 원래 문자열을 그대로 반환하는 로직 확인
        val invalidDate = "NotADate"
        val result = DateCalculator.formatDisplayDate(invalidDate)

        assertEquals("NotADate", result)
    }

    @Test
    fun `formatDisplayDate with null or blank should return empty string`() {
        assertEquals("", DateCalculator.formatDisplayDate(null))
        assertEquals("", DateCalculator.formatDisplayDate(""))
    }

    @Test
    fun `getDateRange should return correct start and end dates`() {
        val baseDate = "20250514"
        val days = 3

        val (start, end) = DateCalculator.getDateRange(baseDate, days)

        // 5월 14일 - 3일 = 11일
        assertEquals("20250511", start)
        // 5월 14일 + 3일 = 17일
        assertEquals("20250517", end)
    }

    @Test
    fun `getDateRange invalid baseDate should fallback to today`() {
        val invalidBase = "invalid_date"
        val days = 1

        val (start, end) = DateCalculator.getDateRange(invalidBase, days)

        assert(start.isNotEmpty() && end.isNotEmpty()) //오늘 날짜가 나와야함
    }


    @Test
    fun `getAllDatesInRange should return all dates between start and end`() {
        val start = "20250528"
        val end = "20250602" // 달이 넘어가는 경우 테스트

        val result = DateCalculator.getAllDatesInRange(start, end)

        val expected = listOf("20250528", "20250529", "20250530", "20250531", "20250601", "20250602")
        assertEquals(expected, result)
        assertEquals(6, result.size)
    }


    @Test
    fun `getAllDatesInRange invalid range should return empty list`() {
        // 시작일이 종료일보다 늦은 경우
        val start = "20250520"
        val end = "20250510"

        val result = DateCalculator.getAllDatesInRange(start, end)

        assertEquals(0, result.size)
        assert(result.isEmpty())
    }

    @Test
    fun `getAllDatesInRange - null or empty input should return empty list`() {
        assertEquals(emptyList<String>(), DateCalculator.getAllDatesInRange("", "20250514"))
        assertEquals(emptyList<String>(), DateCalculator.getAllDatesInRange("20250514", ""))
    }


    @Test
    fun `getMonthRange should return first and last day of the month`() {
        val calendar = Calendar.getInstance().apply {
            set(2025, Calendar.FEBRUARY, 10)
        }

        val (start, end) = DateCalculator.getMonthRange(calendar.time)

        assertEquals("20250201", start)
        assertEquals("20250228", end)
    }


    @Test
    fun `getMonthRange should handle leap year correctly`() {
        val calendar = Calendar.getInstance().apply {
            set(2024, Calendar.FEBRUARY, 10) //윤년
        }

        val (start, end) = DateCalculator.getMonthRange(calendar.time)

        assertEquals("20240201", start)
        assertEquals("20240229", end) // 29일이어야함
    }


    @Test
    fun `getRelativeMonth should return correct shifted month`() {
        val calendar = Calendar.getInstance().apply {
            set(2025, Calendar.JANUARY, 31)
        }

        val nextMonth = DateCalculator.getRelativeMonth(calendar.time, 1)
        val result = DateCalculator.formatMonth(nextMonth)

        assertEquals("2월", result)
    }


    @Test
    fun `getRelativeMonth should handle year transition`() { //연도가 바뀔때 연월이 잘 나오는지
        val calendar = Calendar.getInstance().apply {
            set(2025, Calendar.DECEMBER, 15)
        }

        val nextMonthDate = DateCalculator.getRelativeMonth(calendar.time, 1)
        val monthStr = DateCalculator.formatMonth(nextMonthDate)

        assertEquals("1월", monthStr)

        val apiStr = DateCalculator.formatForApi(nextMonthDate)
        assert(apiStr.startsWith("2026"))
    }


    @Test
    fun `getDayOfWeek should return 1 for Monday and 0 for Sunday`() {
        assertEquals(1, DateCalculator.getDayOfWeek("20250512")) // 월
        assertEquals(3, DateCalculator.getDayOfWeek("20250514")) // 수
        assertEquals(5, DateCalculator.getDayOfWeek("20250516")) // 금
        assertEquals(0, DateCalculator.getDayOfWeek("20250518")) // 일 (0 반환 로직 확인)
    }


    @Test
    fun `getDayOfWeek invalid date should return 0`() {
        assertEquals(0, DateCalculator.getDayOfWeek("invalid"))
        assertEquals(0, DateCalculator.getDayOfWeek(""))
    }
}