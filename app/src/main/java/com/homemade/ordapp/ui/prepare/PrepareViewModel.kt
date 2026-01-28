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

@RequiresApi(Build.VERSION_CODES.O)
class PrepareViewModel(
    private val orderRepository: OrderRepository = Graph.orderRepository,
): ViewModel() {
    companion object {
        private const val TAG = "class PrepareViewModel(\n"
    }
    private val _state = MutableStateFlow(
        PrepareViewState(
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
        )
    }
    data class PrepareViewState(
        val refreshing: Boolean = false,
    )

}