package com.homemade.ordapp.data.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.homemade.ordapp.data.room.entities.Order
import com.homemade.ordapp.data.room.entities.OrderItem
import com.homemade.ordapp.data.room.model.OrderWithItem
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDAO  {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: Order): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrderItems(items: List<OrderItem>)

    @Query("DELETE FROM order_items WHERE parentOrderId = :orderId")
    suspend fun deleteItemsByOrderId(orderId: Long)

    @Transaction
    suspend fun createFullOrder(order: Order, items: List<OrderItem>) {
        val newOrderId = insertOrder(order)
        val itemsWithId = items.map { it.copy(parentOrderId = newOrderId) }
        insertOrderItems(itemsWithId)
    }

    @Transaction
    @Query("SELECT * FROM orders ORDER BY orderTime DESC")
    fun getAllOrdersWithItems(): Flow<List<OrderWithItem>>

    @Transaction
    @Query("SELECT * FROM orders WHERE orderId = :orderId")
    suspend fun getOrderById(orderId: Long): OrderWithItem

    @Transaction
    @Query("SELECT * FROM orders WHERE pickupTime = :date ORDER BY orderTime DESC")
    fun getOrderByDate(date: String): Flow<List<OrderWithItem>>

    @Transaction
    @Query("SELECT * FROM orders WHERE status = :status ORDER BY pickupTime ASC")
    fun getOrdersByStatus(status: String): Flow<List<OrderWithItem>>

    @Update
    suspend fun updateOrder(order: Order)

    @Transaction
    suspend fun updateFullOrder(order: Order, newItems: List<OrderItem>) {
        updateOrder(order)

        deleteItemsByOrderId(order.orderId)

        val itemsWithId = newItems.map { it.copy(parentOrderId = order.orderId) }
        insertOrderItems(itemsWithId)
    }

    @Query("UPDATE orders SET status = :newStatus WHERE orderId = :id")
    suspend fun updateOrderStatus(id: Long, newStatus: String)

    @Delete
    suspend fun deleteOrder(order: Order)

    @Query("DELETE FROM orders WHERE status = 'COMPLETED'")
    suspend fun clearOldOrders()
}