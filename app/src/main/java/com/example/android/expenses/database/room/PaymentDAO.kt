package com.example.android.expenses.database.room

import androidx.room.*
import com.example.android.expenses.model.Payment
import kotlinx.coroutines.flow.Flow

@Dao
interface PaymentDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg payment: Payment)

    @Update
    suspend fun update(payment: Payment)

    @Query("SELECT * from payments WHERE id = :key")
    suspend fun get(key: Int): Payment?

    @Delete
    suspend fun delete(name: Payment)

    @Query("DELETE FROM payments")
    suspend fun clear()

    @Query("SELECT * FROM payments")
    fun getAllElements(): Flow<List<Payment>>
}