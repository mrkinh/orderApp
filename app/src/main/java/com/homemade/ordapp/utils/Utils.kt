package com.homemade.ordapp.utils

import android.os.Build
import androidx.annotation.RequiresApi
import com.homemade.ordapp.ui.home.formatTimestamp
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

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
