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
    private val repository: SchoolRepository
) : ViewModel() {

    private val _state = MutableStateFlow(MealServiceUiState())
    val state = _state.asStateFlow()
    var selectedMealType by mutableStateOf("2") // 기본값 점심
    lateinit var dates: Pair<String, String>
    var selectedDate by mutableStateOf(Date())

    private var currentRange: Pair<String, String>? = null

    val errorData = listOf(
        MealRowDto(
            MMEAL_SC_NM = "문제가 \n발생하였습니다.\n잠시 후 \n다시 시도해주세요"
        )
    )

    init {
        val today = DateCalculator.formatForApi(Date())
        val range = DateCalculator.getDateRange(today, 60)
        currentRange = range
        fetchMealData(range.first, range.second)
    }

    var showNutritionDialog by mutableStateOf(false)
        private set

    fun openNutritionDialog() {
        showNutritionDialog = true
    }

    fun closeNutritionDialog() {
        showNutritionDialog = false
    }

    fun updateSelectedDate(dateStr: String) {
        val date = DateCalculator.parseApiDate(dateStr)
        if (date != null) {
            selectedDate = date
        }
    }

    fun updateMealType(newType: String) {
        if (selectedMealType == newType) return

        selectedMealType = newType

        val from = currentRange?.first ?: dates.first
        val to = currentRange?.second ?: dates.second

        fetchMealData(from, to)
    }

    fun fetchMealData(from: String, to: String) {
        currentRange = Pair(from, to) // 현재 범위 저장

        viewModelScope.launch {
            _state.value = _state.value.copy(loading = true)

            repository.getMealServiceInfo(
                atptCode = "J10",
                schoolCode = "7530528",
                mealCode = selectedMealType,
                fromYmd = from,
                toYmd = to
            ).onSuccess { apiList ->
                val allDates = DateCalculator.getAllDatesInRange(from, to)

                /** 주말, 공휴일, 급식없는 날 등 데이터가 들어오지 않는 날 처리 **/
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
            }
                .onError { code, msg ->
                    _state.value = MealServiceUiState(
                        loading = false,
                        items = errorData,
                        errorMessage = "API ERROR $code: $msg"
                    )
                }
                .onFailure { t ->
                    _state.value = MealServiceUiState(
                        loading = false,
                        items = errorData,
                        errorMessage = t.message ?: "Network Failure"
                    )
                }
        }
    }
}
