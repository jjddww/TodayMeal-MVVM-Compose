package com.dawoon.todaymeal.ui.nav

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.dawoon.todaymeal.R
import com.dawoon.todaymeal.ui.theme.BorderGreen
import com.dawoon.todaymeal.ui.theme.TextDeepGreen
import com.dawoon.todaymeal.ui.theme.TextLightGreen

@Composable
fun BottomAppBar(navController: NavHostController,
                 onHeightPxChanged: (Int) -> Unit) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val isDark = isSystemInDarkTheme()

    val topOnlyShape = RoundedCornerShape(
        topStart = 24.dp,
        topEnd = 24.dp,
        bottomStart = 0.dp,
        bottomEnd = 0.dp
    )

    BottomAppBar(
        modifier = Modifier
            .clip(topOnlyShape)
            .onSizeChanged { onHeightPxChanged(it.height) }
            .border(
                width = 1.dp,
                color = BorderGreen,
                shape = topOnlyShape
            ),
        containerColor = Color.Transparent,
        tonalElevation = 0.dp
    ) {
        bottomItems.take(2).forEach { item ->
            val selected = currentDestination?.hierarchy?.any { it.route == item.route } == true
            val selectedColor = if (isDark) TextLightGreen else TextDeepGreen
            val unSelectedColor = if (isDark) TextDeepGreen else TextLightGreen

            NavigationBarItem(
                selected = selected,
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color.Transparent,
                    selectedTextColor = selectedColor,
                    unselectedTextColor = unSelectedColor,
                    selectedIconColor = Color.Unspecified,
                    unselectedIconColor = Color.Unspecified
                ),
                interactionSource = remember { MutableInteractionSource() },
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    val iconRes = when {
                        selected && isDark -> item.selectedDark
                        selected -> item.selectedLight
                        isDark -> item.dark
                        else -> item.light
                    }

                    Image(
                        painter = painterResource(iconRes),
                        contentDescription = item.label,
                        modifier = Modifier.size(35.dp)
                    )
                },
                label = { Text(item.label) },
                alwaysShowLabel = true
            )
        }

        Spacer(Modifier.width(60.dp))

        bottomItems.drop(2).forEach { item ->
            val selected = currentDestination?.hierarchy?.any { it.route == item.route } == true
            val selectedColor = if (isDark) TextLightGreen else TextDeepGreen
            val unSelectedColor = if (isDark) TextDeepGreen else TextLightGreen

            NavigationBarItem(
                selected = selected,
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color.Transparent,
                    selectedTextColor = selectedColor,
                    unselectedTextColor = unSelectedColor,
                    selectedIconColor = Color.Unspecified,
                    unselectedIconColor = Color.Unspecified
                ),
                interactionSource = remember { MutableInteractionSource() },
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    val iconRes = when {
                        selected && isDark -> item.selectedDark
                        selected -> item.selectedLight
                        isDark -> item.dark
                        else -> item.light
                    }

                    Image(
                        painter = painterResource(iconRes),
                        contentDescription = item.label,
                        modifier = Modifier.size(35.dp)
                    )
                },
                label = { Text(item.label) },
                alwaysShowLabel = true
            )
        }
    }
}


@Composable
fun HomeFab(
    navController: NavHostController,
    bottomBarHeight: Dp
) {
    Box(
        modifier = Modifier
            .offset(y = bottomBarHeight * 0.57f)
            .size(110.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                navController.navigate(HOME_ROUTE) {
                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(R.drawable.icn_home),
            contentDescription = "홈",
            modifier = Modifier.fillMaxSize() // 또는 적절한 사이즈
        )
    }
}