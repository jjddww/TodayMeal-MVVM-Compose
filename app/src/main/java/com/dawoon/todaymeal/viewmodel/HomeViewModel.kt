package com.dawoon.todaymeal.viewmodel

import com.dawoon.todaymeal.network.model.SchoolRowDto
import com.dawoon.todaymeal.repository.SchoolRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dawoon.todaymeal.network.onError
import com.dawoon.todaymeal.network.onFailure
import com.dawoon.todaymeal.network.onSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SchoolUiState(
    val loading: Boolean = false,
    val items: List<SchoolRowDto> = emptyList(),
    val errorMessage: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: SchoolRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SchoolUiState())
    val state = _state.asStateFlow()

    fun searchSchool(name: String) {
        if (name.isBlank()) {
            _state.value = SchoolUiState(
                loading = false,
                items = emptyList(),
                errorMessage = "학교 이름을 입력하세요"
            )
            return
        }

        viewModelScope.launch {
            _state.value = _state.value.copy(loading = true, errorMessage = null)

            repository.searchSchool(name)
                .onSuccess { list ->
                    _state.value = SchoolUiState(
                        loading = false,
                        items = list,
                        errorMessage = null
                    )
                }
                .onError { code, msg ->
                    _state.value = SchoolUiState(
                        loading = false,
                        items = emptyList(),
                        errorMessage = "API ERROR $code: $msg"
                    )
                }
                .onFailure { t ->
                    _state.value = SchoolUiState(
                        loading = false,
                        items = emptyList(),
                        errorMessage = t.message ?: "Network Failure"
                    )
                }
        }
    }
}
