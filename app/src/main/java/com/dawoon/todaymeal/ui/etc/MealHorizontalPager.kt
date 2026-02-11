package com.dawoon.todaymeal.ui.etc

import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import com.dawoon.todaymeal.network.model.MealRowDto
import kotlin.math.absoluteValue



@Composable
fun MealHorizontalPager(
    cards: List<MealRowDto>,
    modifier: Modifier = Modifier,
    pagerState: PagerState
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val cardWidth = 261.dp
    val horizontalPadding = (screenWidth - cardWidth) / 2

    HorizontalPager(
        state = pagerState,
        pageSize = PageSize.Fixed(cardWidth),
        snapPosition = SnapPosition.Center, // 중앙 정렬 유지
        contentPadding = PaddingValues(horizontal = horizontalPadding), // 계산된 패딩 적용
        pageSpacing = 20.dp,
        userScrollEnabled = cards.size > 1,
        modifier = modifier.fillMaxWidth().height(360.dp),
        verticalAlignment = Alignment.CenterVertically
    ) { page ->
        // 현재 페이지와 떨어진 정도 계산
        val pageOffset = ((pagerState.currentPage - page) + pagerState.currentPageOffsetFraction).absoluteValue

        val scale = lerp(
            start = 0.85f,
            stop = 1f,
            fraction = 1f - pageOffset.coerceIn(0f, 1f)
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                    alpha = 1f
                },
            contentAlignment = Alignment.Center
        ) {
            MealCardView(
                modifier = Modifier
                    .width(cardWidth)
                    .height(327.dp),
                meal = cards[page]
            )
        }
    }
}