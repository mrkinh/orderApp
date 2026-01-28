package com.homemade.ordapp.ui

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.navigation.compose.NavHost
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.composable
import com.homemade.ordapp.Graph
import com.homemade.ordapp.ui.home.Home
import com.homemade.ordapp.ui.order.CreateOrder
import com.homemade.ordapp.ui.order.OrderList
import com.homemade.ordapp.ui.prepare.Prepare
import com.homemade.ordapp.ui.statistic.Statistic

@RequiresApi(Build.VERSION_CODES.R)
@Composable
fun SOrdApp (
    appState: SOrdAppState  = rememberSOrdAppState()
) {
    Graph.screenHeightDp = LocalConfiguration.current.screenHeightDp
    Graph.screenWidthDp = LocalConfiguration.current.screenWidthDp
    val context = LocalContext.current
    var startScreen = Screen.Home.route

    NavHost(
        navController = appState.navController,
        startDestination =  startScreen
    ) {
        composable(Screen.Home.route) { backStackEntry ->
            Graph.homeVM.refresh()
            Home(appState.navController, appState::navigateToOther)
        }

        composable(Screen.Prepare.route) { backStackEntry ->
            Prepare(appState.navController, appState::navigateToOther)
        }

        composable(Screen.List.route) { backStackEntry ->
            Graph.orderVM.refresh()
            OrderList(appState.navController, appState::navigateToOther)
        }

        composable(Screen.CreateOrder.route) { backStackEntry ->
            CreateOrder(appState.navController, appState::navigateToOther)
        }

        composable(Screen.Statistic.route) { backStackEntry ->
            Graph.statisticVM.refresh()
            Statistic(appState.navController, appState::navigateToOther)
        }
    }
}
