package com.homemade.ordapp.ui

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

@Composable
fun rememberSOrdAppState(
    navController: NavHostController = rememberNavController(),
    context: Context = LocalContext.current
) = remember(navController, context) {
    SOrdAppState(navController, context)
}

class SOrdAppState(
    val navController: NavHostController,
    private val context: Context
) {
    fun navigateBack() {
        navController.popBackStack()
    }

    fun navigateToOther(to: String) {
        navController.navigate(to) {
            launchSingleTop = true
            restoreState = true
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
        }
    }
}