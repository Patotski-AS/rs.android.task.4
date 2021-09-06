package com.example.android.expenses.database.room

import androidx.room.*
import com.example.android.expenses.model.Payment
import kotlinx.coroutines.flow.Flow
import com.example.android.expenses.database.*
import kotlinx.coroutines.flow.distinctUntilChanged

@Dao
interface PaymentDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg payment: Payment)

    @Update
    suspend fun update(payment: Payment)

    @Query("SELECT * FROM $DB_PAYMENTS WHERE id = :key")
    suspend fun get(key: Int): Payment?

    @Delete
    suspend fun delete(name: Payment)

    @Query("DELETE FROM $DB_PAYMENTS")
    suspend fun clear()

    @Query("SELECT * FROM payments")
    fun getAllElements(): Flow<List<Payment>>
}