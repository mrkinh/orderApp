package com.homemade.ordapp.utils

import android.os.Build
import androidx.annotation.RequiresApi
import com.homemade.ordapp.ui.home.formatTimestamp

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