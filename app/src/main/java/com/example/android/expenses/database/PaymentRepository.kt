package com.example.android.expenses.database

import android.content.Context
import androidx.preference.PreferenceManager
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.example.android.expenses.database.room.PaymentDAO
import com.example.android.expenses.model.Payment
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PaymentRepository(
    private val paymentDAO: PaymentDAO,
    context: Context
) {
    private val preferences = PreferenceManager.getDefaultSharedPreferences(context)
    private val asSort = preferences.getBoolean("sort", false)
    private val sort = preferences.getString("list_sort", ID).toString().trim()
    private val filter = preferences.getString("category", NONE).toString().trim()
    private val asFilter = preferences.getBoolean("filter", true)

    fun allPayments(): Flow<List<Payment>> {
        return if (asFilter) {
            paymentDAO.getFilter(filter).map { sortListPayments(it) }
        } else
            paymentDAO.getAllElements().map { sortListPayments(it) }
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(payment: Payment) {
        paymentDAO.insert(payment)
    }

    fun getPaymentForDB(id: Int): Flow<Payment> {
        return paymentDAO.getDogDistinctUntilChanged(id)
    }

    suspend fun deletePayment(payment: Payment) {
        paymentDAO.delete(payment)
    }

    suspend fun updatePayment(payment: Payment) {
        paymentDAO.update(payment)
    }

    private fun sortListPayments(list: List<Payment>): List<Payment> {
        return if (asSort) {
            when (sort) {
                PAYMENT_NAME -> list.sortedBy { it.name }
                PAYMENT_COST -> list.sortedBy { it.cost }
                PAYMENT_CATEGORY -> list.sortedBy { it.category }
                PAYMENT_DATE -> list.sortedBy { it.date }
                else -> list.sortedBy { it.id }
            }
        } else {
            when (sort) {
                PAYMENT_NAME -> list.sortedByDescending { it.name }
                PAYMENT_COST -> list.sortedByDescending { it.cost }
                PAYMENT_CATEGORY -> list.sortedByDescending { it.category }
                PAYMENT_DATE -> list.sortedByDescending { it.date }
                else -> list.sortedByDescending { it.id }
            }
        }
    }
}