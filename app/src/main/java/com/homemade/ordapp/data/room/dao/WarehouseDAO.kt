package com.homemade.ordapp.data.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.homemade.ordapp.data.room.entities.Warehouse
import kotlinx.coroutines.flow.Flow

@Dao
interface WarehouseDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(warehouse: Warehouse): Long

    @Delete
    suspend fun delete(warehouse: Warehouse)

    @Query("DELETE FROM warehouse WHERE warehouseId = :id")
    suspend fun deleteById(id: Long)

    @Query("""
        UPDATE warehouse SET 
        item1_quantity = CASE WHEN item1_name = :itemName THEN :newQuantity ELSE item1_quantity END,
        item2_quantity = CASE WHEN item2_name = :itemName THEN :newQuantity ELSE item2_quantity END,
        item3_quantity = CASE WHEN item3_name = :itemName THEN :newQuantity ELSE item3_quantity END,
        item4_quantity = CASE WHEN item4_name = :itemName THEN :newQuantity ELSE item4_quantity END,
        item5_quantity = CASE WHEN item5_name = :itemName THEN :newQuantity ELSE item5_quantity END,
        item6_quantity = CASE WHEN item6_name = :itemName THEN :newQuantity ELSE item6_quantity END,
        item7_quantity = CASE WHEN item7_name = :itemName THEN :newQuantity ELSE item7_quantity END
        WHERE warehouseId = :id
    """)
    suspend fun updateItemQuantityByName(id: Long, itemName: String, newQuantity: Int)

    @Query("SELECT * FROM warehouse ORDER BY date DESC")
    fun getAll(): Flow<List<Warehouse>>

    @Query("SELECT * FROM warehouse WHERE date = :date ORDER BY date DESC")
    fun getByDate(date: String): Flow<List<Warehouse>>
}