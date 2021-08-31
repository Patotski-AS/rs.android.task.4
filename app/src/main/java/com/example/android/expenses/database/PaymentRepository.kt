package com.example.android.expenses.database

import androidx.annotation.WorkerThread
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.example.android.expenses.database.room.PaymentDAO
import com.example.android.expenses.model.Payment
import kotlinx.coroutines.flow.Flow

class PaymentRepository(private val paymentDAO: PaymentDAO) {

    val allPayments: Flow<List<Payment>> = paymentDAO.getAllElements()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert( payment: Payment) {
        paymentDAO.insert(payment)
    }

    suspend fun getPayment( id:Int) {
        paymentDAO.get(id)
    }

    suspend fun clear() {
        paymentDAO.clear()
    }

    suspend fun deletePayment( payment: Payment) {
        paymentDAO.delete(payment)
    }

    suspend fun updatePayment( payment: Payment) {
        paymentDAO.update(payment)
    }

}