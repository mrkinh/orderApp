package com.homemade.ordapp.data.repository

import com.homemade.ordapp.data.room.dao.OrderDAO
import com.homemade.ordapp.data.room.entities.Order
import com.homemade.ordapp.data.room.entities.OrderItem
import com.homemade.ordapp.data.room.model.OrderWithItem
import kotlinx.coroutines.flow.Flow

class OrderRepository (private val orderDAO: OrderDAO) {
    fun getAll(): Flow<List<OrderWithItem>> {
        return orderDAO.getAllOrdersWithItems()
    }

    fun getByDate(date: String): Flow<List<OrderWithItem>> {
        return orderDAO.getOrderByDate(date)
    }

    suspend fun createFullOrder(order: Order, items: List<OrderItem>) {
        return orderDAO.createFullOrder(order, items)
    }

    suspend fun updateOrderStatus(orderId: Long, status: String) {
        return orderDAO.updateOrderStatus(orderId, status)
    }

    suspend fun updateFullOrder(order: Order, newItems: List<OrderItem>) {
        return orderDAO.updateFullOrder(order, newItems)
    }
}