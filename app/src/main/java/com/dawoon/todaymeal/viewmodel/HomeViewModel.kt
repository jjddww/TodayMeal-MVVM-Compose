package com.dawoon.todaymeal.viewmodel

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.dawoon.todaymeal.network.model.SchoolRowDto
import com.dawoon.todaymeal.repository.SchoolRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dawoon.todaymeal.network.model.MealRowDto
import com.dawoon.todaymeal.network.onError
import com.dawoon.todaymeal.network.onFailure
import com.dawoon.todaymeal.network.onSuccess
import com.dawoon.todaymeal.util.DateCalculator
import com.dawoon.todaymeal.util.PreferenceManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

data class MealServiceUiState(
    val loading: Boolean = false,
    val items: List<MealRowDto> = emptyList(),
    val errorMessage: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: SchoolRepository,
    private val prefManager: PreferenceManager
) : ViewModel() {

    val mockToday = "20250514"
    private val _state = MutableStateFlow(MealServiceUiState())
    val state = _state.asStateFlow()

    var selectedMealType by mutableStateOf("2")
//    var selectedDate by mutableStateOf(Date())

    var selectedDate by mutableStateOf(DateCalculator.parseApiDate(mockToday) ?: Date()) /** 임시 데이터!! **/

    private var currentRange: Pair<String, String>? = null

    var schoolName by mutableStateOf(prefManager.getSchoolName())

    private val errorData = listOf(
        MealRowDto(MMEAL_SC_NM = "문제가 \n발생하였습니다.\n잠시 후 \n다시 시도해주세요")
    )

    init {
        val todayStr = DateCalculator.formatForApi(Date())
//        val range = DateCalculator.getDateRange(todayStr, 60)
        /** 임시 데이터!! **/
        val range = DateCalculator.getDateRange(mockToday, 60)
        fetchMealData(range.first, range.second)
    }

    var showNutritionDialog by mutableStateOf(false)
        private set

    fun openNutritionDialog() { showNutritionDialog = true }
    fun closeNutritionDialog() { showNutritionDialog = false }

    fun updateSelectedDate(dateStr: String) {
        DateCalculator.parseApiDate(dateStr)?.let {
            selectedDate = it
        }
    }

    fun updateMealType(newType: String) {
        if (selectedMealType == newType) return
        selectedMealType = newType

        // currentRange가 null일 가능성을 대비해 안전하게 호출
        currentRange?.let {
            fetchMealData(it.first, it.second)
        }
    }

    fun resetSetting() {
        prefManager.clearAll()
    }

    fun fetchMealData(from: String, to: String) {
        currentRange = Pair(from, to)

        viewModelScope.launch {
            // 로딩 시작 시 기존 데이터를 유지하면서 상태만 변경 (화면 깜빡임 방지)
            _state.value = _state.value.copy(loading = true, errorMessage = null)

            repository.getMealServiceInfo(
                atptCode = prefManager.getAtptCode(),
                schoolCode = prefManager.getSchoolCode(),
                mealCode = selectedMealType,
                fromYmd = from,
                toYmd = to
            ).onSuccess { apiList ->
                val allDates = DateCalculator.getAllDatesInRange(from, to)

                val fullList = allDates.map { dateStr ->
                    apiList.find { it.MLSV_YMD == dateStr } ?: MealRowDto(
                        MLSV_YMD = dateStr,
                        DDISH_NM = "급식 정보가 없습니다.",
                        MMEAL_SC_NM = when(selectedMealType) {
                            "1" -> "아침" "2" -> "점심" "3" -> "저녁" else -> ""
                        }
                    )
                }
                _state.value = MealServiceUiState(items = fullList, loading = false)
            }.onError { code, msg ->
                _state.value = MealServiceUiState(
                    loading = false,
                    items = errorData,
                    errorMessage = "API ERROR $code: $msg"
                )
            }.onFailure { t ->
                _state.value = MealServiceUiState(
                    loading = false,
                    items = errorData,
                    errorMessage = t.message ?: "Network Failure"
                )
            }
        }
    }

    // 혹시라도 오늘로 돌아가기 버튼을 만든다면 사용
    val initialPageIndex = derivedStateOf {
        val today = DateCalculator.formatForApi(Date())
        val index = state.value.items.indexOfFirst { it.MLSV_YMD == today }
        if (index == -1) 0 else index
    }
}
