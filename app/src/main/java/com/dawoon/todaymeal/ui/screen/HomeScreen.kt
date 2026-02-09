package com.dawoon.todaymeal.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dawoon.todaymeal.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    var query by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            label = { Text("학교 이름") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { viewModel.searchSchool(query) },
            enabled = !state.loading
        ) {
            Text("검색")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 로딩
        if (state.loading) {
            Text("로딩 중...")
        }

        // 에러
        state.errorMessage?.let {
            Text(text = it)
        }

        // ✅ 결과값 그냥 Text로 출력
        if (state.items.isNotEmpty()) {
            Text(
                text = state.items.joinToString("\n") { school ->
                    """
                    학교명: ${school.SCHUL_NM}
                    교육청: ${school.ATPT_OFCDC_SC_NM}
                    학교코드: ${school.SD_SCHUL_CODE}
                    ------------------------
                    """.trimIndent()
                }
            )
        }
    }
}
