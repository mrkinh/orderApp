package com.homemade.ordapp.data.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "orders")
data class Order(
    @PrimaryKey(autoGenerate = true) val orderId: Long = 0,

    val customerName: String,
    val customerPhone: String,
    val pickupTime: String,
    val orderTime: String,
    val depositMoney: String,
    val totalMoney: String,
    val description: String,
    val status: String = "PENDING"
)