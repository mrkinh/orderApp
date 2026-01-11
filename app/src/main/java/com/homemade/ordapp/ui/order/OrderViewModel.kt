package com.homemade.ordapp.ui.order

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.homemade.ordapp.Graph
import com.homemade.ordapp.data.repository.OrderRepository
import com.homemade.ordapp.data.room.model.OrderWithItem
import com.homemade.ordapp.ui.home.HomeViewModel.HomeUIItem
import com.homemade.ordapp.utils.ITEM_CHUNG_CAKE_LARGE
import com.homemade.ordapp.utils.ITEM_CHUNG_CAKE_NORMAL
import com.homemade.ordapp.utils.ITEM_CHUNG_CAKE_SMALL
import com.homemade.ordapp.utils.ITEM_PORK_SAUSAGE
import com.homemade.ordapp.utils.ITEM_PORK_SAUSAGE_FRY
import com.homemade.ordapp.utils.ITEM_PORK_SAUSAGE_FRY_LARGE
import com.homemade.ordapp.utils.ITEM_PORK_SAUSAGE_LARGE
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

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
            orderRepository.getAll().collect { orders ->
                _state.update { it.copy(orderList = orders.toMutableList()) }
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
        )
    }
    data class OrderViewState(
        val orderList: MutableList<OrderWithItem>,
        val refreshing: Boolean = false,
    )
}