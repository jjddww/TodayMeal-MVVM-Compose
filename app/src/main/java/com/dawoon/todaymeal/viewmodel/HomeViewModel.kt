package com.dawoon.todaymeal.viewmodel

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
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
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
        private set

    fun updateMealType(newType: String) {
        selectedMealType = newType
        getMealService()
    }

    fun getMealService() {

        val emptyData = listOf(
            MealRowDto(
                MMEAL_SC_NM = "식단 정보를\n 찾을 수 없습니다."
            )
        )

        val errorData = listOf(
            MealRowDto(
                MMEAL_SC_NM = "문제가 \n발생하였습니다.\n잠시 후 \n다시 시도해주세요"
            )
        )


        viewModelScope.launch {
            _state.value = _state.value.copy(loading = true, errorMessage = null)

            repository.getMealServiceInfo(
                atptCode = "J10",
                schoolCode = "7530528",
                mealCode = selectedMealType,
                fromYmd = "20250501",
                toYmd = "20250630"
            )
                .onSuccess { list -> val data = list.ifEmpty { emptyData }

                _state.value = MealServiceUiState(
                    loading = false,
                    items = data,
                    errorMessage = null
                )
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

//        viewModelScope.launch {
//            _state.value = _state.value.copy(loading = true, errorMessage = null)
//
//            repository.searchSchool(name)
//                .onSuccess { list ->
//                    _state.value = SchoolUiState(
//                        loading = false,
//                        items = list,
//                        errorMessage = null
//                    )
//                }
//                .onError { code, msg ->
//                    _state.value = SchoolUiState(
//                        loading = false,
//                        items = emptyList(),
//                        errorMessage = "API ERROR $code: $msg"
//                    )
//                }
//                .onFailure { t ->
//                    _state.value = SchoolUiState(
//                        loading = false,
//                        items = emptyList(),
//                        errorMessage = t.message ?: "Network Failure"
//                    )
//                }
//        }
    }
}
