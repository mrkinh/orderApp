package com.homemade.ordapp

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.Room
import com.homemade.ordapp.data.repository.OrderRepository
import com.homemade.ordapp.data.repository.WarehouseRepository
import com.homemade.ordapp.data.room.OrdAppDatabase
import com.homemade.ordapp.ui.home.HomeViewModel
import com.homemade.ordapp.ui.order.OrderViewModel
import com.homemade.ordapp.ui.statistic.StatisticViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

object Graph {
    lateinit var database: OrdAppDatabase
    lateinit var homeVM: HomeViewModel
    lateinit var orderVM: OrderViewModel
    lateinit var statisticVM: StatisticViewModel

    var screenWidthDp: Int = 0
    var screenHeightDp: Int = 0
    private val TAG="Graph"

    val mainDispatcher: CoroutineDispatcher
        get() = Dispatchers.Main

    val ioDispatcher: CoroutineDispatcher
        get() = Dispatchers.IO

    val orderRepository by lazy {
        OrderRepository(
            orderDAO = database.orderDao(),
        )
    }
    val warehouseRepository by lazy {
        WarehouseRepository(
            warehouseDAO = database.warehouseDao(),
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun provide(context: Context) {
        database = Room.databaseBuilder(context, OrdAppDatabase::class.java, "OrdAppDB.db")
            .fallbackToDestructiveMigration()
            .build()

        homeVM = HomeViewModel()
        orderVM = OrderViewModel()
        statisticVM = StatisticViewModel()
    }
}