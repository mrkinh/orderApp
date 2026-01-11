package com.homemade.ordapp.data.repository

import com.homemade.ordapp.data.room.dao.OrderDAO
import com.homemade.ordapp.data.room.model.OrderWithItem
import kotlinx.coroutines.flow.Flow

class OrderRepository (private val orderDAO: OrderDAO) {
    fun getAll(): Flow<List<OrderWithItem>> {
        return orderDAO.getAllOrdersWithItems()
    }

    fun getByDate(date: String): Flow<List<OrderWithItem>> {
        return orderDAO.getOrderByDate(date)
    }
}