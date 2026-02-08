package com.homemade.ordapp.ui.statistic

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.homemade.ordapp.Graph
import com.homemade.ordapp.data.repository.OrderRepository
import com.homemade.ordapp.data.repository.WarehouseRepository
import com.homemade.ordapp.data.room.entities.OrderItem
import com.homemade.ordapp.data.room.model.OrderWithItem
import com.homemade.ordapp.ui.home.HomeViewModel.HomeUIItem
import com.homemade.ordapp.utils.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.apache.poi.ss.usermodel.VerticalAlignment
import java.io.File
import java.io.FileOutputStream

@RequiresApi(Build.VERSION_CODES.O)
class StatisticViewModel(
    private val orderRepository: OrderRepository = Graph.orderRepository,
    private val warehouseRepository: WarehouseRepository = Graph.warehouseRepository,
): ViewModel() {
    companion object {
        private const val TAG = "StatisticViewModel"
    }
    private val _state = MutableStateFlow(
        StatisticViewState(
            totalPrice = "0K",
            orderList = mutableListOf(),
            statisticItem = mutableListOf()
        )
    )
    val state: StateFlow<StatisticViewState>
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
                val itemList = updateStatisticItems(orders)
                _state.update { it.copy(
                    totalPrice = getTotalPrice(itemList).toString(),
                    orderList = orders,
                    statisticItem = itemList)
                }
            }
        }
    }

    private fun updateStatisticItems(orders: List<OrderWithItem>): List<StatisticItem> {
        val dates = getDateList()
        val ret: MutableList<StatisticItem> = mutableListOf()
        dates.forEach { date ->
            var item = StatisticItem(
                date = date,
                totalPrice = "",
                items = createDefaultItemList()
            )
            var orderedListOfDate = orders.filter { it.order.pickupTime == date && it.order.status != ORDER_STATUS_CANCELED }
            val totalsMap = orderedListOfDate
                .flatMap { it.items }
                .groupBy { it.itemName }
                .mapValues { entry -> entry.value.sumOf { it.quantity } }

            val newDetailItem = createDefaultItemList().map { defaultItem ->
                defaultItem.copy(
                    quantity = totalsMap[defaultItem.name] ?: 0
                )
            }
            item = item.copy( items = newDetailItem, totalPrice = getTotalPriceOfDate(newDetailItem).toString())
            ret.add(item)
        }
        return ret
    }

    private fun createDefaultItemList(): MutableList<StatisticItemDetail> {
        return mutableListOf(
            StatisticItemDetail(name = ITEM_CHUNG_CAKE_SMALL, quantity = 0),
            StatisticItemDetail(name = ITEM_CHUNG_CAKE_NORMAL, quantity = 0),
            StatisticItemDetail(name = ITEM_CHUNG_CAKE_LARGE, quantity = 0),
            StatisticItemDetail(name = ITEM_PORK_SAUSAGE, quantity = 0),
            StatisticItemDetail(name = ITEM_PORK_SAUSAGE_LARGE, quantity = 0),
            StatisticItemDetail(name = ITEM_PORK_SAUSAGE_FRY, quantity = 0),
            StatisticItemDetail(name = ITEM_PORK_SAUSAGE_FRY_LARGE, quantity = 0)
        )
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun exportToExcel(context: Context, file: String) {
        val orders = _state.value.orderList
        val workbook = XSSFWorkbook()

        val wrapStyle = workbook.createCellStyle().apply {
            wrapText = true
            verticalAlignment = VerticalAlignment.TOP
        }
        val groupedOrders = orders.groupBy { it.order.pickupTime }

        groupedOrders.forEach { (dateLabel, ordersInGroup) ->
            var dateLabelFinal = dateLabel.replace("/", "-")
            val sheet = workbook.createSheet(dateLabelFinal)

            val headerRow = sheet.createRow(0)
            val columns = listOf("Ten", "SDT", "Coc", "Ghi Chu", "Ngay Lay", "Chi Tiet", )
            columns.forEachIndexed { index, title -> headerRow.createCell(index).setCellValue(title) }

            var rowIdx = 1
            ordersInGroup
                .filter { it.order.status != ORDER_STATUS_CANCELED }
                .forEach { orderWithItem ->
                val row = sheet.createRow(rowIdx++)

                val safeDate = orderWithItem.order.pickupTime.replace("/", "-")
                row.createCell(0).setCellValue(orderWithItem.order.customerName)
                row.createCell(1).setCellValue(orderWithItem.order.customerPhone)
                row.createCell(2).setCellValue(orderWithItem.order.depositMoney)
                row.createCell(3).setCellValue(orderWithItem.order.description)
                row.createCell(4).setCellValue(safeDate)

                val detailString = orderWithItem.items.joinToString(separator = "\n") { detail ->
                    "${getReadableItemName(detail.itemName)}: ${detail.quantity}"
                }
                val detailCell = row.createCell(5)
                detailCell.setCellValue(detailString)
                detailCell.setCellStyle(wrapStyle)
                val lineCount = orderWithItem.items.count()
                row.height = (lineCount * 14 * 20).toShort()
            }
            sheet.setColumnWidth(5, 15000)
        }

        val resolver = context.contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, file)
            put(MediaStore.MediaColumns.MIME_TYPE, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
        }

        val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
        uri?.let {
            resolver.openOutputStream(it).use { outputStream ->
                workbook.write(outputStream)
            }
        }
        workbook.close()
    }

    fun getTotalPriceOfDate(items: List<StatisticItemDetail>): Int {
        val totalCakeLarge = items.filter { it.name == ITEM_CHUNG_CAKE_LARGE }.sumOf { it.quantity }
        val totalCakeNormal = items.filter { it.name == ITEM_CHUNG_CAKE_NORMAL }.sumOf { it.quantity }
        val totalCakeSmall = items.filter { it.name == ITEM_CHUNG_CAKE_SMALL }.sumOf { it.quantity }
        val totalSausageLarge= items.filter { it.name == ITEM_PORK_SAUSAGE_LARGE }.sumOf { it.quantity }
        val totalSausage = items.filter { it.name == ITEM_PORK_SAUSAGE }.sumOf { it.quantity }
        val totalSausageFry = items.filter { it.name == ITEM_PORK_SAUSAGE_FRY }.sumOf { it.quantity }
        return (totalCakeLarge * 120) + (totalCakeNormal * 70) + (totalCakeSmall * 50) + (totalSausageLarge * 180) + (totalSausage * 90) + (totalSausageFry * 90)
    }

    fun getTotalPrice(items: List<StatisticItem>): Int {
        var totalPrice = 0
        items.forEach { detail ->
            totalPrice += getTotalPriceOfDate(detail.items)
        }
        return totalPrice
    }

    fun getDateList(): MutableList<String> {
        return mutableListOf(
            "10/02/2026",
            "11/02/2026",
            "12/02/2026",
            "13/02/2026",
            "14/02/2026",
            "15/02/2026",
            "16/02/2026",
            "17/02/2026",
        )
    }

    data class StatisticItem(
        val date: String,
        val totalPrice: String,
        val items: List<StatisticItemDetail>
    )

    data class StatisticItemDetail(
        val name: String,
        val quantity: Int
    )

    data class StatisticViewState(
        val orderList: List<OrderWithItem>,
        val statisticItem: List<StatisticItem>,
        val totalPrice: String,
        val refreshing: Boolean = false,
    )
}