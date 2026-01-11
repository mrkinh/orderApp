package com.homemade.ordapp.data.room.entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.homemade.ordapp.data.room.model.WarehouseItem

@Entity(tableName = "warehouse")
data class Warehouse(
    @PrimaryKey(autoGenerate = true) val warehouseId: Long = 0,
    val date: String,

    @Embedded(prefix = "item1_") val item1: WarehouseItem,
    @Embedded(prefix = "item2_") val item2: WarehouseItem,
    @Embedded(prefix = "item3_") val item3: WarehouseItem,
    @Embedded(prefix = "item4_") val item4: WarehouseItem,
    @Embedded(prefix = "item5_") val item5: WarehouseItem,
    @Embedded(prefix = "item6_") val item6: WarehouseItem,
    @Embedded(prefix = "item7_") val item7: WarehouseItem
)