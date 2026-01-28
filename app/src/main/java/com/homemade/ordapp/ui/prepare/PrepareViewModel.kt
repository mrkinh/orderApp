package com.homemade.ordapp.ui.prepare

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.homemade.ordapp.Graph
import com.homemade.ordapp.data.repository.OrderRepository
import com.homemade.ordapp.data.room.model.OrderWithItem
import com.homemade.ordapp.utils.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import androidx.compose.runtime.*
import com.homemade.ordapp.data.repository.WarehouseRepository
import com.homemade.ordapp.data.room.entities.Warehouse
import com.homemade.ordapp.data.room.model.WarehouseItem

@RequiresApi(Build.VERSION_CODES.O)
class PrepareViewModel(
    private val warehouseRepository: WarehouseRepository = Graph.warehouseRepository,
): ViewModel() {
    companion object {
        private const val TAG = "class PrepareViewModel(\n"
    }
    private val _state = MutableStateFlow(
        PrepareViewState(
            creatingItem = Warehouse(
                date = "17/02/2026",
                item1 = WarehouseItem(name = ITEM_CHUNG_CAKE_LARGE, quantity = 0),
                item2 = WarehouseItem(name = ITEM_CHUNG_CAKE_NORMAL, quantity = 0),
                item3 = WarehouseItem(name = ITEM_CHUNG_CAKE_SMALL, quantity = 0),
                item4 = WarehouseItem(name = ITEM_PORK_SAUSAGE_LARGE, quantity = 0),
                item5 = WarehouseItem(name = ITEM_PORK_SAUSAGE, quantity = 0),
                item6 = WarehouseItem(name = ITEM_PORK_SAUSAGE_FRY_LARGE, quantity = 0),
                item7 = WarehouseItem(name = ITEM_PORK_SAUSAGE_FRY, quantity = 0)
            )
        )
    )
    val state: StateFlow<PrepareViewState>
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

    fun updateCreatingItemDate(date: String) {
        _state.update { it.copy(creatingItem = it.creatingItem.copy(date = date)) }
    }

    fun updateCreatingItem(name: String, quantity: Int) {
        _state.update { currentState ->
            val item = WarehouseItem(name, quantity)
            val updatedWarehouse = when (name) {
                currentState.creatingItem.item1.name -> currentState.creatingItem.copy(item1 = item)
                currentState.creatingItem.item2.name -> currentState.creatingItem.copy(item2 = item)
                currentState.creatingItem.item3.name -> currentState.creatingItem.copy(item3 = item)
                currentState.creatingItem.item4.name -> currentState.creatingItem.copy(item4 = item)
                currentState.creatingItem.item5.name -> currentState.creatingItem.copy(item5 = item)
                currentState.creatingItem.item6.name -> currentState.creatingItem.copy(item6 = item)
                currentState.creatingItem.item7.name -> currentState.creatingItem.copy(item7 = item)
                else -> currentState.creatingItem
            }
            currentState.copy(creatingItem = updatedWarehouse)
        }
    }

    fun updateItem() {
        _state.update { it.copy(refreshing = true) }
        viewModelScope.launch(Graph.ioDispatcher) {
            warehouseRepository.createOrUpdate(_state.value.creatingItem)
            _state.update { it.copy(refreshing = false, creatingItem = Warehouse(
                date = "17/02/2026",
                item1 = WarehouseItem(name = ITEM_CHUNG_CAKE_LARGE, quantity = 0),
                item2 = WarehouseItem(name = ITEM_CHUNG_CAKE_NORMAL, quantity = 0),
                item3 = WarehouseItem(name = ITEM_CHUNG_CAKE_SMALL, quantity = 0),
                item4 = WarehouseItem(name = ITEM_PORK_SAUSAGE_LARGE, quantity = 0),
                item5 = WarehouseItem(name = ITEM_PORK_SAUSAGE, quantity = 0),
                item6 = WarehouseItem(name = ITEM_PORK_SAUSAGE_FRY_LARGE, quantity = 0),
                item7 = WarehouseItem(name = ITEM_PORK_SAUSAGE_FRY, quantity = 0))) }
        }
    }

    data class PrepareViewState(
        val creatingItem: Warehouse,
        val refreshing: Boolean = false,
    )
}