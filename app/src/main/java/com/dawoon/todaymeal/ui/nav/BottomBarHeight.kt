package com.dawoon.todaymeal.ui.nav

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class BottomBarHeightState(
    val height: Dp,
    val onHeightPxChanged: (heightPx: Int) -> Unit
)

@Composable
fun rememberBottomBarHeightState(): BottomBarHeightState {
    val density = LocalDensity.current
    val heightState = remember { mutableStateOf(0.dp) }

    return BottomBarHeightState(
        height = heightState.value,
        onHeightPxChanged = { heightPx ->
            heightState.value = with(density) { heightPx.toDp() }
        }
    )
}