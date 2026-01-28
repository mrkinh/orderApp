package com.homemade.ordapp.ui.order

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.homemade.ordapp.Graph
import com.homemade.ordapp.data.repository.OrderRepository
import com.homemade.ordapp.data.room.entities.Order
import com.homemade.ordapp.data.room.entities.OrderItem
import com.homemade.ordapp.data.room.model.OrderWithItem
import com.homemade.ordapp.utils.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date

@RequiresApi(Build.VERSION_CODES.O)
class OrderViewModel(
    private val orderRepository: OrderRepository = Graph.orderRepository,
): ViewModel() {
    companion object {
        private const val TAG = "OrderViewModel"
    }
    private val _state = MutableStateFlow(
        OrderViewState(
            orderList = mutableListOf(),
            displayOrderList = mutableListOf(),
            creatingOrder = OrderWithItem(
                order = Order(),
                items = emptyList()
            ),
            editingOrder = OrderWithItem(
                order = Order(),
                items = emptyList()
            ),
            searchStatus = SearchStatus(
                date = getCurrentDate()
            )
        )
    )
    val state: StateFlow<OrderViewState>
        get() = _state

    init {
        refresh()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun refresh() {
        Log.d(TAG, "[refresh] called")
        refreshAllData()
    }

    private fun refreshAllData() {
        _state.update { it.copy(refreshing = true) }
        viewModelScope.launch(Graph.ioDispatcher) {
            orderRepository.getByDate(getCurrentDate()).collect { orders ->
                _state.update { it.copy(
                    orderList = orders.toMutableList(),
                    displayOrderList=orders.toMutableList(),
                    searchStatus = SearchStatus(
                        date = getCurrentDate()
                    ),
                    refreshing = false
                )}
            }
        }
    }
    fun getDateList(): MutableList<String> {
        return mutableListOf(
            "11/02/2026",
            "12/02/2026",
            "13/02/2026",
            "14/02/2026",
            "15/02/2026",
            "16/02/2026",
            "17/02/2026",
        )
    }

    fun getSearchDateList(): MutableList<String> {
        return mutableListOf(
            "11/02/2026",
            "12/02/2026",
            "13/02/2026",
            "14/02/2026",
            "15/02/2026",
            "16/02/2026",
            "17/02/2026",
            "ALL",
        )
    }

    fun addOrUpdateItem(newItem: OrderItem) {
        _state.update { currentState ->
            val currentOrder = currentState.creatingOrder
            val itemsList = currentOrder.items

            val itemExists = itemsList.any { it.itemName == newItem.itemName }
            val updatedItems = if (itemExists) {
                itemsList.map { item ->
                    if (item.itemName == newItem.itemName) {
                        item.copy(quantity = newItem.quantity)
                    } else {
                        item
                    }
                }
            } else {
                itemsList + newItem
            }
            val filteredItems = updatedItems.filter { it.quantity > 0 }
            currentState.copy(
                creatingOrder = currentOrder.copy(items = filteredItems)
            )
        }
    }

    fun updateOrderInfo(newOrder: Order): Int {
        if (_state.value.refreshing) return 1

        _state.update { currentState ->
            val currentOrder = currentState.creatingOrder
            currentState.copy(
                creatingOrder = currentOrder.copy(order = newOrder),
                refreshing = false
            )
        }
        return 0
    }

    fun addNewOder(): Int {
        if (_state.value.refreshing) return 1

        _state.update { it.copy(refreshing = true) }
        val currentState = _state.value
        val currentTime = Date()
        var finalOrder = currentState.creatingOrder.order.copy(orderTime = currentTime, status = ORDER_STATUS_ORDERED)
        val newOrder = currentState.creatingOrder.copy(order = finalOrder)

        viewModelScope.launch(Graph.ioDispatcher) {
            orderRepository.createFullOrder(newOrder.order, newOrder.items)
            _state.update { it.copy(creatingOrder = OrderWithItem(
                order = Order(),
                items = emptyList()
            ), refreshing = false) }
        }
        return 0
    }

    fun isCreatingOrderValid(): Boolean {
        if (_state.value.creatingOrder.order.customerName.isEmpty()) return false
        if (_state.value.creatingOrder.order.customerPhone.isEmpty()) return false
        return true
    }

    fun setUpdatingOrder(newOrder: Order) {
        val currentState = _state.value
        var newOrderFinal = currentState.editingOrder.copy( order = newOrder)
        _state.update { it.copy( editingOrder = newOrderFinal) }
    }
    fun updateOrder(): Int {
        if (_state.value.refreshing) return 1

        _state.update { it.copy(refreshing = true) }
        viewModelScope.launch(Graph.ioDispatcher) {
            orderRepository.updateOrderStatus(_state.value.editingOrder.order.orderId, _state.value.editingOrder.order.status)
            _state.update { it.copy(editingOrder = OrderWithItem(
                order = Order(),
                items = emptyList()
            ), refreshing = false) }
        }
        return 0
    }

    fun getOrderStatusList(): MutableList<Pair<String, String>> {
        return mutableListOf(
            ORDER_STATUS_ORDERED to "Chưa Giao" ,
            ORDER_STATUS_DELIVERED to "Đã Giao" ,
            ORDER_STATUS_CANCELED to "Đã Hủy" ,
        )
    }

    fun filterByOrderStatus(status: String) {
        val filteredList = _state.value.orderList.filter { it.order.status ==  status}
        _state.update { it.copy(
            displayOrderList = filteredList.toMutableList(),
            searchStatus = it.searchStatus.copy(
                orderStatus = status
            )
        )}
    }

    fun filterByDate(date: String) {
        _state.update { it.copy(refreshing = true) }
        viewModelScope.launch(Graph.ioDispatcher) {
            if (date == "ALL") {
                orderRepository.getAll().collect { orders ->
                    _state.update {
                        it.copy(
                            orderList = orders.toMutableList(),
                            displayOrderList = orders.toMutableList(),
                            searchStatus = SearchStatus(
                                date = date
                            ),
                            refreshing = false
                        )
                    }
                }
            } else {
                orderRepository.getByDate(date).collect { orders ->
                    _state.update {
                        it.copy(
                            orderList = orders.toMutableList(),
                            displayOrderList = orders.toMutableList(),
                            searchStatus = SearchStatus(
                                date = date
                            ),
                            refreshing = false
                        )
                    }
                }
            }
        }
    }

    fun filterByKeyword(keyword: String) {
        val filteredList = _state.value.orderList.filter { it.order.customerName.contains(keyword.trim(), true) or it.order.customerPhone.contains(keyword.trim(), true)}
        _state.update { it.copy(
            displayOrderList = filteredList.toMutableList(),
            searchStatus = it.searchStatus.copy(
                keyword = keyword
            )
        )}
    }

    data class SearchStatus(
        var keyword: String = "",
        var date: String = getCurrentDate(),
        var orderStatus: String = ORDER_STATUS_UNKNOWN,
    )
    data class OrderViewState(
        val orderList: MutableList<OrderWithItem>,
        val displayOrderList: MutableList<OrderWithItem>,
        val creatingOrder: OrderWithItem,
        val editingOrder: OrderWithItem,
        val searchStatus: SearchStatus,
        val refreshing: Boolean = false,
    )
}