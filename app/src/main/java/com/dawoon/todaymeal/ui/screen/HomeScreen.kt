package com.dawoon.todaymeal.ui.screen

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
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
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
import java.sql.Date

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel()
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
            // currentMeal에서 영양정보(NTR_INFO)를 꺼내 전달
            carInfo = currentMeal?.CAL_INFO ?: "정보 없음",
            nutritionInfo = currentMeal?.NTR_INFO ?: "영양 정보가 없습니다.",
            onDismiss = { viewModel.closeNutritionDialog() }
        )
    }

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
                    .background(if (isDark) DarkBackground else Color.White)
        ) {

            Text(
                text = "성문고등학교",
                modifier =
                    Modifier
                        .align(Alignment.Center),
                color = textColor,
                fontSize = 24.sp,
                fontFamily = FontFamily(
                    Font(resId = R.font.suite_extrabold)
                )
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
