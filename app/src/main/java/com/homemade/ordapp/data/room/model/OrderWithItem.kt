package com.homemade.ordapp.data.room.model

import androidx.room.Embedded
import androidx.room.Relation
import com.homemade.ordapp.data.room.entities.Order
import com.homemade.ordapp.data.room.entities.OrderItem

data class OrderWithItem(
    @Embedded val order: Order,
    @Relation(
        parentColumn = "orderId",
        entityColumn = "parentOrderId"
    )
    val items: List<OrderItem>
)