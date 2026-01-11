package com.homemade.ordapp.ui.components

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.homemade.ordapp.Graph
import com.homemade.ordapp.ui.Screen
import com.homemade.ordapp.R
import com.homemade.ordapp.ui.theme.textColorPrimary
import com.homemade.ordapp.ui.theme.textColorSecondary

const val ITEM_HEIGHT = 64

sealed class NavItem(var route: String, var icon: Int,  var selectedIcon: Int, var title: String) {
    object Home : NavItem(Screen.Home.route, R.drawable.home, R.drawable.home_focus,"Trang Chủ")
    object List : NavItem(Screen.List.route, R.drawable.list, R.drawable.list_forcus, "Danh Sách")
    object Prepare : NavItem(Screen.Prepare.route, R.drawable.prepare, R.drawable.prepare, "Chuẩn Bị")
    object Statistic : NavItem(Screen.Statistic.route, R.drawable.statistic, R.drawable.statistic, "Thống Kê")
}

@Composable
fun NavBarItem(
    icon: Int,
    label: String,
    isSelected: Boolean,
    navigateToOther: () -> Unit
) {
    var width = getItemWidth()
    var height = getItemHeight()
    var imageSize = height/3

    var textColor = textColorPrimary
    if (isSelected) textColor = textColorSecondary

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .height(height.dp)
            .width(width.dp)
            .clickable(onClick = { navigateToOther() })
    ) {
        Image(
            painter = painterResource(icon),
            contentDescription = "",
            modifier = Modifier
                .size(imageSize.dp)
        )
        Text(
            text = label,
            color = textColor,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
        )
    }
}

@Composable
fun NavBar(
    navController: NavHostController,
    navigateToOther: (String) -> Unit
) {
    val items = listOf(
        NavItem.Home,
        NavItem.List,
        NavItem.Prepare,
        NavItem.Statistic
    )
    val currentRoute = currentRoute(navController)
    Box(
        modifier = Modifier
            .height((getItemHeight() * 1.5).dp)
            .background(color = Color.White)
    ) {
        Row() {
            items.forEachIndexed { index, item ->
                var isSelected = false
                var icon = item.icon
                if (item.route == currentRoute) {
                    isSelected = true
                    icon = item.selectedIcon
                }
                NavBarItem(icon, item.title, isSelected) {
                    navigateToOther(item.route)
                }
            }
        }
    }
}

@Composable
private fun currentRoute(navController: NavHostController): String? {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute =
        navBackStackEntry?.destination?.route ?: Screen.Home.route
    return currentRoute
}

fun getItemWidth(): Int {
    return Graph.screenWidthDp/4
}

fun getItemHeight(): Int {
    return Graph.screenHeightDp/12
}