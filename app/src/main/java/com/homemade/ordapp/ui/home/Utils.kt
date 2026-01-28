package com.homemade.ordapp.ui.home

import android.os.Build
import androidx.annotation.RequiresApi
import com.homemade.ordapp.utils.*
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

// Helper functions

fun getIndexInList(itemName: String): Int {
    return when(itemName) {
        ITEM_CHUNG_CAKE_SMALL -> ITEM_CHUNG_CAKE_SMALL_INDEX
        ITEM_CHUNG_CAKE_NORMAL -> ITEM_CHUNG_CAKE_NORMAL_INDEX
        ITEM_CHUNG_CAKE_LARGE -> ITEM_CHUNG_CAKE_LARGE_INDEX
        ITEM_PORK_SAUSAGE -> ITEM_PORK_SAUSAGE_INDEX
        ITEM_PORK_SAUSAGE_LARGE -> ITEM_PORK_SAUSAGE_LARGE_INDEX
        ITEM_PORK_SAUSAGE_FRY -> ITEM_PORK_SAUSAGE_FRY_INDEX
        ITEM_PORK_SAUSAGE_FRY_LARGE -> ITEM_PORK_SAUSAGE_FRY_LARGE_INDEX
        else -> -1
    }
}