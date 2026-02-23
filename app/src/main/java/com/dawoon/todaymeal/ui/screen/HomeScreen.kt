package com.dawoon.todaymeal.ui.screen

import android.R.attr.data
import android.content.ClipData
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.dawoon.todaymeal.R
import com.dawoon.todaymeal.ui.etc.MealHorizontalPager
import com.dawoon.todaymeal.ui.etc.MealTypeDropdown
import com.dawoon.todaymeal.ui.etc.NutritionDialog
import com.dawoon.todaymeal.ui.theme.BorderGreen
import com.dawoon.todaymeal.ui.theme.DarkBackground
import com.dawoon.todaymeal.ui.theme.DarkList
import com.dawoon.todaymeal.ui.theme.DarkSubBg
import com.dawoon.todaymeal.ui.theme.DarkText
import com.dawoon.todaymeal.ui.theme.LightList
import com.dawoon.todaymeal.ui.theme.LightSubBg
import com.dawoon.todaymeal.ui.theme.LightText
import com.dawoon.todaymeal.util.DateCalculator
import com.dawoon.todaymeal.viewmodel.HomeViewModel
import kotlinx.coroutines.launch
import androidx.core.net.toUri
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dawoon.todaymeal.AppWidget
import com.dawoon.todaymeal.ui.theme.EmailAreaDark
import com.dawoon.todaymeal.ui.theme.EmailAreaLight
import com.dawoon.todaymeal.ui.theme.TextDeepGreen
import com.dawoon.todaymeal.util.WidgetUtil

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    navController: NavController
) {
    val isDark = isSystemInDarkTheme()
    val textColor = if (isDark) DarkText else LightText
    val headerBg = if (isDark) DarkBackground else Color.White // 상태바/헤더 배경 (흰/검)
    val subColor = if (isDark) DarkSubBg else LightSubBg
    val listColor = if (isDark) DarkList else LightList
    val mealData by viewModel.state.collectAsState()
    val selectedType = viewModel.selectedMealType
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(
        initialPage = if (mealData.items.isNotEmpty()) mealData.items.size / 2 else 0
    ) {
        mealData.items.size
    }


    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    var showResetDialog by remember { mutableStateOf(false) }
    val schoolName by viewModel.schoolName.collectAsStateWithLifecycle()

    var showContactDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val clipboardManager = LocalClipboard.current
    val emailAddress = "jdwoon10@gmail.com"

    LaunchedEffect(pagerState.currentPage) {
        mealData.items.getOrNull(pagerState.currentPage)?.let { meal ->
            viewModel.updateSelectedDate(meal.MLSV_YMD ?: "")
        }
    }

    LaunchedEffect(mealData.items) {
        if (mealData.items.isNotEmpty()) {
            val targetDateStr = DateCalculator.formatForApi(viewModel.selectedDate)
            val targetIndex = mealData.items.indexOfFirst { it.MLSV_YMD == targetDateStr }

            if (targetIndex != -1) {
                pagerState.scrollToPage(targetIndex)
            }
        }
    }

    val currentMeal = mealData.items.getOrNull(pagerState.currentPage)

    // 다이얼로그 표시 로직
    if (viewModel.showNutritionDialog) {
        NutritionDialog(
            carInfo = currentMeal?.CAL_INFO ?: "정보 없음",
            nutritionInfo = currentMeal?.NTR_INFO ?: "영양 정보가 없습니다.",
            onDismiss = { viewModel.closeNutritionDialog() }
        )
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = {
                Text(
                    text = "설정 초기화",
                    fontSize = 17.sp,
                    color = textColor,
                    fontFamily = FontFamily(Font(R.font.suite_bold)),
                    fontWeight = FontWeight.Bold)
            },
            text = {
                Text(
                    text = "저장된 학교 및 학년/반 정보가\n모두 삭제됩니다.\n정말 초기화하시겠습니까?",
                    fontSize = 15.sp,
                    color = textColor,
                    fontFamily = FontFamily(Font(R.font.suite_medium))
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showResetDialog = false
                        scope.launch {
                            viewModel.resetSetting()
                            WidgetUtil.updateAllWidgets(context)
                        }
                        navController.navigate("setting") {
                            popUpTo(0)
                        }
                    }
                ) {
                    Text(
                        "예",
                        color = Color.Red,
                        fontFamily = FontFamily(Font(R.font.suite_semibold)),
                        fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text(
                        "아니오",
                        color = textColor,
                        fontFamily = FontFamily(Font(R.font.suite_semibold)),
                        fontWeight = FontWeight.Bold)
                }
            },
            containerColor = if (isDark) DarkBackground else Color.White
        )
    }


    if (showContactDialog) {
        AlertDialog(
            onDismissRequest = { showContactDialog = false },
            containerColor = if (isDark) DarkBackground else Color.White,
            title = {
                Text(
                    "문의사항이 있으신가요?",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily(Font(R.font.suite_bold)),
                    color = textColor
                )
            },
            text = {
                Column {
                    Text(
                        "아래의 주소를 눌러\n메일로 의견을 남겨주세요!",
                        fontSize = 14.sp,
                        color = textColor,
                        fontFamily = FontFamily(Font(R.font.suite_medium))
                    )
                    Spacer(modifier = Modifier.height(20.dp))

                    // 메일 주소 클릭 영역
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                if (isDark) EmailAreaDark else EmailAreaLight,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable {
                                scope.launch {
                                    val clipEntry = ClipEntry(
                                        ClipData.newPlainText("email", emailAddress)
                                    )
                                    clipboardManager.setClipEntry(clipEntry)
                                }

                                val intent = Intent(Intent.ACTION_SENDTO).apply {
                                    data = "mailto:$emailAddress".toUri()
                                    putExtra(Intent.EXTRA_SUBJECT, "[오늘의 급식] 문의사항")
                                }

                                try {
                                    context.startActivity(intent)
                                    Toast.makeText(context, "메일 앱으로 연결합니다.", Toast.LENGTH_SHORT).show()
                                } catch (e: Exception) {
                                    Toast.makeText(context, "메일 앱이 없어 주소가 복사되었습니다.", Toast.LENGTH_SHORT).show()
                                }
                            }
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = emailAddress,
                            color = TextDeepGreen,
                            fontFamily = FontFamily(Font(R.font.suite_semibold)),
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showContactDialog = false }) {
                    Text("닫기",
                        color = textColor,
                        fontFamily = FontFamily(Font(R.font.suite_medium))
                    )
                }
            }
        )
    }


    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = if (isDark) DarkBackground else Color.White,
                modifier = Modifier.width(250.dp)) {

                Spacer(Modifier.height(60.dp))
                Text(
                    "설정",
                    modifier = Modifier.padding(16.dp),
                    fontSize = 20.sp,
                    fontFamily = FontFamily(Font(R.font.suite_bold)),
                    color = textColor
                )

                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

                NavigationDrawerItem(
                    label = {
                        Text(
                            "설정 초기화",
                            fontSize = 17.sp,
                            fontFamily = FontFamily(Font(R.font.suite_semibold)),
                            color = textColor
                        )
                    },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        showResetDialog = true
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                    colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent)
                )

                NavigationDrawerItem(
                    label = {
                        Text(
                            "문의 사항",
                            fontSize = 17.sp,
                            fontFamily = FontFamily(Font(R.font.suite_semibold)),
                            color = textColor
                        )
                    },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        showContactDialog = true
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                    colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent)
                )

                NavigationDrawerItem(
                    label = {
                        Text(
                            "개인정보처리방침",
                            fontSize = 17.sp,
                            fontFamily = FontFamily(Font(R.font.suite_semibold)),
                            color = textColor
                        )
                    },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                    colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent)
                )
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {

            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsTopHeight(WindowInsets.statusBars)
                    .background(headerBg)
            )

            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                        .background(if (isDark) DarkBackground else Color.White),
                contentAlignment = Alignment.Center
            ) {

                IconButton(
                    onClick = { scope.launch { drawerState.open() } },
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 12.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.icn_setting),
                        contentDescription = "Menu",
                        tint = textColor,
                        modifier = Modifier.size(30.dp)
                    )
                }

                Text(
                    text = schoolName,
                    modifier = Modifier.padding(horizontal = 64.dp),
                    color = textColor,
                    fontSize = if (schoolName.length > 10) 18.sp else 24.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontFamily = FontFamily(Font(resId = R.font.suite_extrabold))
                )

            }

            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .background(headerBg)
            ) {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .background(subColor)
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 24.dp),
                ) {

                    Row(
                        modifier =
                            Modifier.padding(horizontal = 18.dp),
                    ) {

                        Icon(
                            painter = painterResource(id = R.drawable.icn_left_arrow),
                            contentDescription = "left arrow",
                            tint = Color.White,
                            modifier = Modifier
                                .size(25.dp)
                                .clickable(enabled = pagerState.currentPage > 0) {
                                    scope.launch {
                                        pagerState.animateScrollToPage(pagerState.currentPage - 1)
                                    }
                                }
                        )

                        Text(
                            text = DateCalculator.formatToDisplay(viewModel.selectedDate),
                            color = Color.White,
                            fontSize = 22.sp,
                            fontFamily = FontFamily(
                                Font(resId = R.font.suite_bold)
                            ),
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center
                        )


                        Icon(
                            painter = painterResource(id = R.drawable.icn_right_arrow),
                            contentDescription = "left arrow",
                            tint = Color.White,
                            modifier = Modifier
                                .size(25.dp)
                                .clickable(enabled = pagerState.currentPage < mealData.items.size - 1) {
                                    scope.launch {
                                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                    }
                                }
                        )

                    }

                    MealTypeDropdown(
                        selectedType = selectedType,
                        onTypeSelected = { newName ->
                            val typeCode = when(newName) {
                                "아침" -> "1"
                                "점심" -> "2"
                                "저녁" -> "3"
                                else -> "2"
                            }
                            viewModel.updateMealType(typeCode)
                        }
                    )

                    MealHorizontalPager(
                        cards = mealData.items,
                        modifier = Modifier
                            .fillMaxWidth(),
                        pagerState = pagerState,
                    )


                    Box(
                        modifier = Modifier
                            .width(156.dp)
                            .height(58.dp)
                            .background(color = listColor, shape = RoundedCornerShape(18.dp))
                            .border(width = 1.dp, color = BorderGreen, shape = RoundedCornerShape(18.dp))
                            .align(Alignment.CenterHorizontally)
                            .clickable {
                                if (currentMeal != null) {
                                    viewModel.openNutritionDialog()
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {

                        Row(modifier = Modifier
                            .fillMaxSize(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center){
                            Image(painter = painterResource(R.drawable.icn_nutrition),
                                contentDescription = "nutrition icon")

                            Spacer(modifier = Modifier.width(11.dp))

                            Text(
                                text = "영양정보",
                                fontSize = 22.sp,
                                fontFamily = FontFamily(Font(R.font.suite_bold)),
                                color = textColor,
                                textAlign = TextAlign.Center
                            )
                        }

                    }
                }
            }
        }

    }
}
