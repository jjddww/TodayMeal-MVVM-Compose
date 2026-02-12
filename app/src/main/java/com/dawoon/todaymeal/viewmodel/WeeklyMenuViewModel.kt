package com.dawoon.todaymeal.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dawoon.todaymeal.network.model.MealRowDto
import com.dawoon.todaymeal.network.model.WeekRange
import com.dawoon.todaymeal.network.onFailure
import com.dawoon.todaymeal.network.onSuccess
import com.dawoon.todaymeal.repository.SchoolRepository
import com.dawoon.todaymeal.util.DateCalculator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject
@HiltViewModel
class WeeklyMenuViewModel @Inject constructor(
    private val repository: SchoolRepository
): ViewModel(){
    private val _weeks = MutableStateFlow<List<WeekRange>>(emptyList())
    val weeks = _weeks.asStateFlow()

    private val _selectedWeekIndex = MutableStateFlow(0)
    val selectedWeekIndex = _selectedWeekIndex.asStateFlow()

    private val _uiState = MutableStateFlow(MealServiceUiState())
    val uiState = _uiState.asStateFlow()

    var selectedMealType by mutableStateOf("2") // 점심 기본

    init {
        generateWeeksForCurrentMonth()
//        val today = DateCalculator.formatForApi(Date())
        val mockToday = "20260304"

//        val initialIndex = _weeks.value.indexOfFirst { week ->
//            today >= week.startDate && today <= week.endDate
//        }

        val initialIndex = _weeks.value.indexOfFirst { week ->
            mockToday >= week.startDate && mockToday <= week.endDate
        }

        if (initialIndex != -1) {
            _selectedWeekIndex.value = initialIndex
        } else {
            _selectedWeekIndex.value = 0
        }

        loadWeeklyData()
    }

    private fun generateWeeksForCurrentMonth() {
//        val calendar = Calendar.getInstance()

        /**임시로 데이터 보기 위해 강제 고정 **/
        val calendar = Calendar.getInstance().apply {
            set(2026, Calendar.MARCH, 4)
        }

        val targetMonth = calendar.get(Calendar.MONTH)
        val year = calendar.get(Calendar.YEAR)
        calendar.set(year, targetMonth, 1)


        while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
            calendar.add(Calendar.DAY_OF_YEAR, -1)
        }

        val weekList = mutableListOf<WeekRange>()
        var weekNum = 1

        while (true) {
            val monday = DateCalculator.formatForApi(calendar.time)
            val currentWeekMonday = calendar.time

            calendar.add(Calendar.DAY_OF_YEAR, 6)
            val sunday = DateCalculator.formatForApi(calendar.time)

            val daysInTargetMonth = (0..6).count { d ->
                val checkCal = Calendar.getInstance().apply {
                    time = currentWeekMonday
                    add(Calendar.DAY_OF_YEAR, d)
                }
                checkCal.get(Calendar.MONTH) == targetMonth
            }
            if (daysInTargetMonth >= 4) {
                weekList.add(WeekRange("${weekNum}주차", monday, sunday))
                weekNum++
            }

            // 다음 주 월요일로 이동
            calendar.add(Calendar.DAY_OF_YEAR, 1)

            // 다음 주 월요일이 타겟 달보다 훨씬 뒤라면 종료
            if (calendar.get(Calendar.MONTH) > targetMonth || calendar.get(Calendar.YEAR) > year) break
            if (weekNum > 6) break
        }
        _weeks.value = weekList
    }


    fun updateWeekIndex(index: Int) {
        if (_selectedWeekIndex.value == index) return
        _selectedWeekIndex.value = index
        loadWeeklyData()
    }

    fun updateMealType(newType: String) {
        if (selectedMealType == newType) return
        selectedMealType = newType
        loadWeeklyData()
    }

    fun loadWeeklyData() {
        val currentWeek = weeks.value.getOrNull(selectedWeekIndex.value) ?: return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(loading = true)

            repository.getMealServiceInfo(
                atptCode = "J10",
                schoolCode = "7530528",
                mealCode = selectedMealType,
                fromYmd = currentWeek.startDate,
                toYmd = currentWeek.endDate
            ).onSuccess { apiList ->

                val allDates = DateCalculator.getAllDatesInRange(currentWeek.startDate, currentWeek.endDate)

                val fullList = allDates.map { dateStr ->
                    apiList.find { it.MLSV_YMD == dateStr } ?: MealRowDto(
                        MLSV_YMD = dateStr,
                        DDISH_NM = "급식 정보가 없습니다.",
                        MMEAL_SC_NM = when (selectedMealType) {
                            "1" -> "아침"
                            "2" -> "점심"
                            "3" -> "저녁"
                            else -> ""
                        }
                    )
                }

                _uiState.value = MealServiceUiState(items = fullList, loading = false)


            }.onFailure { t ->
                _uiState.value = MealServiceUiState(
                    loading = false,
                    errorMessage = t.message ?: "데이터를 불러오지 못했습니다."
                )
            }
        }
    }
}