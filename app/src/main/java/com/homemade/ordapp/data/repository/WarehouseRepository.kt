package com.homemade.ordapp.data.repository

import com.homemade.ordapp.data.room.dao.WarehouseDAO
import com.homemade.ordapp.data.room.entities.Warehouse
import kotlinx.coroutines.flow.Flow

class WarehouseRepository (private val warehouseDAO: WarehouseDAO) {
    fun getAll(): Flow<List<Warehouse>> {
        return warehouseDAO.getAll()
    }

    fun getByDate(date: String): Flow<List<Warehouse>> {
        return warehouseDAO.getByDate(date)
    }
}