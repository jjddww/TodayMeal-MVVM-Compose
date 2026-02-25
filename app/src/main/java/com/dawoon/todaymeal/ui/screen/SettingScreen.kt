package com.dawoon.todaymeal.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.LocalContext
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.appwidget.updateAll
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.hilt.navigation.compose.hiltViewModel
import com.dawoon.todaymeal.AppWidget
import com.dawoon.todaymeal.R
import com.dawoon.todaymeal.network.model.SchoolRowDto
import com.dawoon.todaymeal.ui.theme.DarkBackground
import com.dawoon.todaymeal.ui.theme.LightBackground
import com.dawoon.todaymeal.ui.theme.SettingContainerColor
import com.dawoon.todaymeal.ui.theme.SettingDark
import com.dawoon.todaymeal.ui.theme.SettingLight
import com.dawoon.todaymeal.ui.theme.SettingTitleDark
import com.dawoon.todaymeal.ui.theme.SettingTitleLight
import com.dawoon.todaymeal.ui.theme.errorRed
import com.dawoon.todaymeal.util.WidgetUtil
import com.dawoon.todaymeal.viewmodel.SettingViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(
    viewModel: SettingViewModel = hiltViewModel(),
    onNavigateToNext: () -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val mainGreen = Color(0xFF6B8A7A)
    val backgroundColor = if (isDark) DarkBackground else Color.White
    val context = androidx.compose.ui.platform.LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "학교를 설정해주세요",
                fontFamily = FontFamily(Font(R.font.suite_extrabold)),
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = if (isDark) SettingTitleDark else SettingTitleLight
            )
            Text(
                text = "(추후 언제든 변경 가능합니다)",
                fontFamily = FontFamily(Font(R.font.suite_semibold)),
                color = if (isDark) SettingDark else SettingLight,
                fontSize = 16.sp,
                modifier = Modifier.padding(top = 8.dp, bottom = 40.dp)
            )

            OutlinedTextField(
                value = viewModel.searchQuery,
                onValueChange = { viewModel.searchQuery = it },
                placeholder = { Text("학교 이름을 입력하세요", color = Color.Gray) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = mainGreen,
                    unfocusedBorderColor = if (isDark) Color.DarkGray else Color.LightGray,
                    focusedContainerColor = if (isDark) SettingContainerColor else Color.Transparent,
                    unfocusedContainerColor = if (isDark) SettingContainerColor else Color.Transparent,
                    cursorColor = mainGreen
                ),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Search
                ),
                keyboardActions = KeyboardActions(
                    onSearch = { viewModel.searchSchool() }
                )
            )

            if (viewModel.errorMessage != "") {
                Text(
                    text = viewModel.errorMessage,
                    color = errorRed,
                    fontSize = 14.sp,
                    fontFamily = FontFamily(Font(R.font.suite_semibold)),
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(if (viewModel.errorMessage != "") 16.dp else 32.dp))

            Button(
                onClick = { viewModel.searchSchool() },
                modifier = Modifier
                    .width(116.dp)
                    .height(61.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = mainGreen),
                enabled = !viewModel.isLoading
            ) {
                if (viewModel.isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("확인", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }

        if (viewModel.showPopup) {
            AlertDialog(
                onDismissRequest = { viewModel.showPopup = false },
                properties = DialogProperties(usePlatformDefaultWidth = false),
                modifier = Modifier.fillMaxWidth(0.9f),
                containerColor = if (isDark) Color(0xFF252525) else Color.White,
                title = {
                    Text(
                        "학교를 선택해주세요",
                        fontSize = 20.sp,
                        fontFamily = FontFamily(Font(R.font.suite_medium)),
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Box(modifier = Modifier.heightIn(max = 400.dp)) {
                        if (viewModel.schoolList.value.isEmpty()) {
                            Text("검색 결과가 없습니다.",
                                modifier = Modifier.padding(vertical = 20.dp),
                                fontSize = 14.sp,
                                fontFamily = FontFamily(Font(R.font.suite_medium)),)
                        } else {
                            LazyColumn {
                                items(viewModel.schoolList.value) { school ->
                                    SchoolItem(
                                        school = school,
                                        onClick = {
                                            viewModel.onSchoolSelected(school)
                                        }
                                    )
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { viewModel.showPopup = false }) {
                        Text("닫기", color = mainGreen)
                    }
                }
            )
        }

        if (viewModel.showGradePopup) {
            AlertDialog(
                onDismissRequest = { viewModel.showGradePopup = false },
                containerColor = if (isDark) SettingContainerColor else Color.White,
                title = {
                    Text("상세 정보를 입력해주세요",
                        fontFamily = FontFamily(Font(R.font.suite_medium)),
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp)
                },
                text = {
                    Column(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("학년", fontFamily = FontFamily(Font(R.font.suite_regular)), color = Color.Gray)
                                OutlinedTextField(
                                    value = viewModel.inputGrade,
                                    onValueChange = { if (it.length <= 1) viewModel.inputGrade = it },
                                    placeholder = { Text("예: 3") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    singleLine = true,
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = mainGreen,
                                        cursorColor = mainGreen
                                    )
                                )
                            }

                            // 반 입력 창
                            Column(modifier = Modifier.weight(1f)) {
                                Text("반", fontFamily = FontFamily(Font(R.font.suite_regular)), color = Color.Gray)
                                OutlinedTextField(
                                    value = viewModel.inputClass,
                                    onValueChange = { if (it.length <= 2) viewModel.inputClass = it },
                                    placeholder = { Text("예: 1") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    singleLine = true,
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = mainGreen,
                                        cursorColor = mainGreen
                                    )
                                )
                            }
                        }
                        Text(
                            text = "* 숫자로만 입력해주세요",
                            fontFamily = FontFamily(Font(R.font.suite_regular)),
                            color = Color.Gray,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.saveFinalSettings {
                                // saveFinalSettings가 완료된 후 실행되는 콜백 (onComplete)
                                CoroutineScope(Dispatchers.Main).launch {
                                    WidgetUtil.updateAllWidgets(context)
                                    onNavigateToNext()
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = mainGreen),
                        enabled = viewModel.inputGrade.isNotBlank() && viewModel.inputClass.isNotBlank(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("설정 완료", fontWeight = FontWeight.Bold, fontFamily = FontFamily(Font(R.font.suite_regular)))
                    }
                }
            )
        }
    }
}

@Composable
fun SchoolItem(school: SchoolRowDto, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp, horizontal = 4.dp)
    ) {
        Text(
            text = school.SCHUL_NM ?: "알 수 없는 학교",
            fontFamily = FontFamily(Font(R.font.suite_medium)),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = school.ORG_RDNMA ?: "",
            fontFamily = FontFamily(Font(R.font.suite_medium)),
            fontSize = 14.sp,
            color = Color.Gray
        )
        Divider(modifier = Modifier.padding(top = 12.dp), thickness = 0.5.dp, color = Color.LightGray.copy(alpha = 0.3f))
    }
}