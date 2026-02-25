package com.dawoon.todaymeal.util

import com.dawoon.todaymeal.WidgetMealMapper
import com.dawoon.todaymeal.network.ApiResult
import com.dawoon.todaymeal.network.model.MealRowDto
import junit.framework.TestCase.assertEquals
import org.junit.Test

class WidgetMealMapperTest {
    @Test
    fun `getNextMealType should cycle through MORNING-LUNCH-DINNER correctly`() {
        assertEquals("LUNCH", WidgetMealMapper.getNextMealType("MORNING"))
        assertEquals("DINNER", WidgetMealMapper.getNextMealType("LUNCH"))
        assertEquals("MORNING", WidgetMealMapper.getNextMealType("DINNER"))
    }

    @Test
    fun `getPrevMealType should cycle through DINNER-LUNCH-MORNING correctly`() {
        assertEquals("DINNER", WidgetMealMapper.getPrevMealType("MORNING"))
        assertEquals("MORNING", WidgetMealMapper.getPrevMealType("LUNCH"))
        assertEquals("LUNCH", WidgetMealMapper.getPrevMealType("DINNER"))
    }

    @Test
    fun `getMealDisplayText should return formatted meal when result is Success and data exists`() {
        val mockData = listOf(MealRowDto(DDISH_NM = "제육볶음<br/>쌀밥"))
        val result = ApiResult.Success(mockData)

        val output = WidgetMealMapper.getMealDisplayText(result, "LUNCH")

        assert(output.contains("제육볶음"))
    }

    @Test
    fun `getMealDisplayText should return no-info message when result is Success but data is empty`() {
        val result = ApiResult.Success(emptyList<MealRowDto>())
        val output = WidgetMealMapper.getMealDisplayText(result, "DINNER")

        assertEquals("DINNER 정보가 없습니다.", output)
    }

    @Test
    fun `getMealDisplayText should return error message when result is Failure`() {
        val result = ApiResult.Failure(Exception("Network failure"))
        val output = WidgetMealMapper.getMealDisplayText(result, "MORNING")

        assertEquals("MORNING 정보가 없습니다.", output)
    }

    @Test
    fun `getMealDisplayText should return error message when result is Error`() {
        val result = ApiResult.Error("500", "Server Error")
        val output = WidgetMealMapper.getMealDisplayText(result, "MORNING")

        assertEquals("MORNING 정보가 없습니다.", output)
    }
}