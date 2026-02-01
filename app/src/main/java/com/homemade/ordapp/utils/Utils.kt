package com.homemade.ordapp.utils

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.ui.graphics.Color
import com.homemade.ordapp.data.room.entities.OrderItem
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

val validDate = setOf(
    "10/02/2026", "11/02/2026", "12/02/2026", "13/02/2026", "14/02/2026", "15/02/2026", "16/02/2026"
)

@RequiresApi(Build.VERSION_CODES.O)
fun formatFullTimestamp(timestamp: Long): String {
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm", Locale.getDefault())

    val dateTime = Instant.ofEpochMilli(timestamp)
        .atZone(ZoneId.systemDefault())
        .toLocalDateTime()

    return dateTime.format(formatter)
}

@RequiresApi(Build.VERSION_CODES.O)
fun getCurrentDateTime(): String {
    return formatFullTimestamp(System.currentTimeMillis())
}

@RequiresApi(Build.VERSION_CODES.O)
fun formatTimestamp(timestamp: Long): String {
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.getDefault())
    val date = Instant.ofEpochMilli(timestamp)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
    return date.format(formatter)
}

@RequiresApi(Build.VERSION_CODES.O)
fun getCurrentDate(): String {
    val date = formatTimestamp(System.currentTimeMillis())
    return if (date in validDate) date else "17/02/2026"
}

@RequiresApi(Build.VERSION_CODES.O)
fun getCurrentMoonDate(): String {
    val ret = when (formatTimestamp(System.currentTimeMillis())) {
        "07/02/2026" -> "20"
        "08/02/2026" -> "21"
        "09/02/2026" -> "22"
        "10/02/2026" -> "23"
        "11/02/2026" -> "24"
        "12/02/2026" -> "25"
        "13/02/2026" -> "26"
        "14/02/2026" -> "27"
        "15/02/2026" -> "28"
        "16/02/2026" -> "29"
        "17/02/2026" -> "01"
        else -> "00"
    }
    return ret
}

fun getMoonDate(date: String): String {
    val ret = when (date) {
        "07/02/2026" -> "20"
        "08/02/2026" -> "21"
        "09/02/2026" -> "22"
        "10/02/2026" -> "23"
        "11/02/2026" -> "24"
        "12/02/2026" -> "25"
        "13/02/2026" -> "26"
        "14/02/2026" -> "27"
        "15/02/2026" -> "28"
        "16/02/2026" -> "29"
        "17/02/2026" -> "01"
        else -> "00"
    }
    return ret
}

fun getReadableItemName(itemName: String): String {
    return when(itemName) {
        ITEM_PORK_SAUSAGE_LARGE -> "Giò (1Kg)"
        ITEM_PORK_SAUSAGE -> "Giò (0.5Kg)"
        ITEM_PORK_SAUSAGE_FRY -> "Chả Chiên"
        ITEM_CHUNG_CAKE_LARGE -> "Bánh Chưng (To)"
        ITEM_CHUNG_CAKE_NORMAL -> "Bánh Chưng (Vừa)"
        ITEM_CHUNG_CAKE_SMALL -> "Bánh Chưng (Nhỏ)"
        else -> ""
    }
}
fun getQuantityTypeName(itemName: String): String {
    return when(itemName) {
        ITEM_PORK_SAUSAGE_FRY -> "Kg"
        else -> "Cái"
    }
}

fun getOrderStatusName(status: String): String {
    return when (status) {
        ORDER_STATUS_UNKNOWN -> "Chưa xác nhận"
        ORDER_STATUS_ORDERED -> "Chưa Giao"
        ORDER_STATUS_DELIVERED -> "Đã Giao"
        ORDER_STATUS_CANCELED -> "Đã Hủy"
        else ->  "Chưa xác nhận"
    }
}

fun getOrderStatusColor(status: String): Color {
    return when (status) {
        ORDER_STATUS_UNKNOWN -> Color(0xFF51AF58)
        ORDER_STATUS_ORDERED -> Color(0xFFFF0000)
        ORDER_STATUS_DELIVERED -> Color(0xFF51AF58)
        ORDER_STATUS_CANCELED -> Color(0xFFE4CA8A)
        else ->  Color(0xFF51AF58)
    }
}

fun getOrderStatusBGColor(status: String): Color {
    return when (status) {
        ORDER_STATUS_UNKNOWN -> Color(0x4C51AF58)
        ORDER_STATUS_ORDERED -> Color(0x4CFF0000)
        ORDER_STATUS_DELIVERED -> Color(0x4C51AF58)
        ORDER_STATUS_CANCELED -> Color(0x4CE4CA8A)
        else ->  Color(0x4C51AF58)
    }
}

fun getTotalPrice(items: List<OrderItem>): Int {
    val totalCakeLarge = items.filter { it.itemName == ITEM_CHUNG_CAKE_LARGE }.sumOf { it.quantity }
    val totalCakeNormal = items.filter { it.itemName == ITEM_CHUNG_CAKE_NORMAL }.sumOf { it.quantity }
    val totalCakeSmall = items.filter { it.itemName == ITEM_CHUNG_CAKE_SMALL }.sumOf { it.quantity }
    val totalSausageLarge= items.filter { it.itemName == ITEM_PORK_SAUSAGE_LARGE }.sumOf { it.quantity }
    val totalSausage = items.filter { it.itemName == ITEM_PORK_SAUSAGE }.sumOf { it.quantity }
    val totalSausageFry = items.filter { it.itemName == ITEM_PORK_SAUSAGE_FRY }.sumOf { it.quantity }
    return (totalCakeLarge * 120) + (totalCakeNormal * 70) + (totalCakeSmall * 50) + (totalSausageLarge * 180) + (totalSausage * 90) + (totalSausageFry * 180)
}