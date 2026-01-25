package com.homemade.ordapp.ui.home

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.homemade.ordapp.Graph
import com.homemade.ordapp.data.repository.OrderRepository
import com.homemade.ordapp.data.repository.WarehouseRepository
import com.homemade.ordapp.data.room.entities.Warehouse
import com.homemade.ordapp.data.room.model.OrderWithItem
import com.homemade.ordapp.utils.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
class HomeViewModel(
    private val orderRepository: OrderRepository = Graph.orderRepository,
    private val warehouseRepository: WarehouseRepository = Graph.warehouseRepository,
): ViewModel() {
    companion object {
        private const val TAG = "HomeViewModel"
    }
    private val _state = MutableStateFlow(
        HomeViewState(
            orderList = mutableListOf(),
            deliveredList = mutableListOf(),
            warehouseList = mutableListOf()
        )
    )
    val state: StateFlow<HomeViewState>
        get() = _state

    init {
        initEmptyList()
        refresh()
    }

    fun initEmptyList() {
        _state.update { it.copy( orderList = createDefaultItemList(),
                                 deliveredList = createDefaultItemList(),
                                 warehouseList = createDefaultItemList()) }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun refresh() {
        Log.d(TAG, "[refresh] called")
        refreshAllData()
    }

    private fun createDefaultItemList(): MutableList<HomeUIItem> {
        return mutableListOf(
            HomeUIItem(name = ITEM_CHUNG_CAKE_SMALL, quantity = 0),
            HomeUIItem(name = ITEM_CHUNG_CAKE_NORMAL, quantity = 0),
            HomeUIItem(name = ITEM_CHUNG_CAKE_LARGE, quantity = 0),
            HomeUIItem(name = ITEM_PORK_SAUSAGE, quantity = 0),
            HomeUIItem(name = ITEM_PORK_SAUSAGE_LARGE, quantity = 0),
            HomeUIItem(name = ITEM_PORK_SAUSAGE_FRY, quantity = 0),
            HomeUIItem(name = ITEM_PORK_SAUSAGE_FRY_LARGE, quantity = 0)
        )
    }

    private fun refreshAllData() {
        _state.update { it.copy(refreshing = true) }
        viewModelScope.launch(Graph.ioDispatcher) {
            val date = getCurrentDate()
            combine(
                orderRepository.getByDate(date),
                warehouseRepository.getByDate(date)
            ) { orders, warehouses ->
                Pair(orders, warehouses)
            }.take(1)
                .collect { (orders, warehouses) ->
                    val deliveredOrders = orders.filter { it.order.status ==  ORDER_STATUS_DELIVERED}
                    val orderedOrders = orders.filter { it.order.status ==  ORDER_STATUS_ORDERED}
                    var orderListUI = extractOrderList(orderedOrders).toMutableList()
                    var deliveredListUI = extractOrderList(deliveredOrders).toMutableList()
                    val warehouseUIItems = extractWarehouseList(warehouses)
                    _state.update { it.copy(warehouseList = warehouseUIItems, orderList = orderListUI, deliveredList = deliveredListUI) }
                }
        }
    }

    private fun extractWarehouseList(warehouses: List<Warehouse>): MutableList<HomeUIItem> {
        val ret = createDefaultItemList()
        warehouses.firstOrNull()?.let { w ->
            val items = listOf(w.item1, w.item2, w.item3, w.item4, w.item5, w.item6, w.item7)
            items.forEach { item ->
                val index = getIndexInList(item.name)
                if (index in 0..6) {
                    ret[index].quantity = item.quantity
                }
            }
        }
        return ret
    }

    private fun extractOrderList(orderedList: List <OrderWithItem>): List<HomeUIItem> {
        val totalsMap = orderedList
            .flatMap { it.items }
            .groupBy { it.itemName }
            .mapValues { entry -> entry.value.sumOf { it.quantity } }

        return createDefaultItemList().map { defaultItem ->
            defaultItem.copy(
                quantity = totalsMap[defaultItem.name] ?: 0
            )
        }
    }

    data class HomeUIItem(
        val name: String,
        var quantity: Int
    )

    data class HomeViewState(
        val orderList: MutableList<HomeUIItem>,
        val deliveredList: MutableList<HomeUIItem>,
        val warehouseList: MutableList<HomeUIItem>,
        val refreshing: Boolean = false,
    )
}