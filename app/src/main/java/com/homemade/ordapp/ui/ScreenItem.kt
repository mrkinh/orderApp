package com.homemade.ordapp.ui

sealed class Screen (val route: String) {
    object Home : Screen("Home")
    object List : Screen("List")
    object CreateOrder : Screen("CreateOrder")
    object UpdateOrder : Screen("UpdateOrder")
    object Prepare : Screen("Prepare")
    object Statistic : Screen("Statistic")
}