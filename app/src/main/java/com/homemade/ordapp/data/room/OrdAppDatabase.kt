package com.homemade.ordapp.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.homemade.ordapp.data.room.dao.OrderDAO
import com.homemade.ordapp.data.room.dao.WarehouseDAO
import com.homemade.ordapp.data.room.entities.Order
import com.homemade.ordapp.data.room.entities.OrderItem
import com.homemade.ordapp.data.room.entities.Warehouse

@Database(
    entities = [Order::class, OrderItem::class, Warehouse::class],
    version = 2,
    exportSchema = false
)

abstract class OrdAppDatabase : RoomDatabase() {
    abstract fun orderDao(): OrderDAO
    abstract fun warehouseDao(): WarehouseDAO
}