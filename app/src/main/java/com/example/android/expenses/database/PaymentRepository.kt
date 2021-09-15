package com.example.android.expenses.database

import android.app.Application
import android.util.Log
import androidx.preference.PreferenceManager
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.example.android.expenses.*
import com.example.android.expenses.database.cursor.CursorHelper
import com.example.android.expenses.database.room.PaymentDAO
import com.example.android.expenses.model.Payment
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PaymentRepository(
    private val paymentDAO: PaymentDAO,
    private val application: Application,
    private val cursorHelper: CursorHelper = CursorHelper(application)
) {
    private val preferences = PreferenceManager.getDefaultSharedPreferences(application)
    private val management = preferences.getString(PREF_LIST_MANAGEMENT, ROOM).toString().trim()
    private val asSort = preferences.getBoolean(PREF_SORT, false)
    private val sort = preferences.getString(PREF_LIST_SORT, ID).toString().trim()
    private val filter = preferences.getString(PREF_CATEGORY, NONE).toString().trim()
    private val asFilter = preferences.getBoolean(PREF_FILTER, false)

    fun allPayments(): Flow<List<Payment>> {
        return if (management == ROOM) {
            if (asFilter) {
                paymentDAO.getFilter(filter).map { sortListPayments(it) }
            } else
                paymentDAO.getAllElements().map { sortListPayments(it) }
        } else {
            if (asFilter) {
                cursorHelper.getPaymentsList(filter).map { sortListPayments(it) }
            } else
                cursorHelper.getPaymentsList(null).map { sortListPayments(it) }
        }
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(payment: Payment) {
        if (management == ROOM)
            paymentDAO.insert(payment)
        else {
            cursorHelper.insert(payment)
        }
    }

    fun getPaymentForDB(id: Int): Flow<Payment?> {
        return if (management == ROOM)
            paymentDAO.getDogDistinctUntilChanged(id)
        else cursorHelper.getPayment(id)
    }

    suspend fun deletePayment(payment: Payment) {
        if (management == ROOM)
            paymentDAO.delete(payment)
        else
            cursorHelper.delete(payment)
    }

    suspend fun updatePayment(payment: Payment) {
        if (management == ROOM)
            paymentDAO.update(payment)
        else
            cursorHelper.update(payment)
    }

    private fun sortListPayments(list: List<Payment>): List<Payment> {
        return if (asSort) {
            when (sort) {
                PAYMENT_NAME -> list.sortedByDescending { it.name }
                PAYMENT_COST -> list.sortedByDescending { it.cost }
                PAYMENT_CATEGORY -> list.sortedByDescending { it.category }
                PAYMENT_DATE -> list.sortedByDescending { it.date }
                else -> list.sortedByDescending { it.id }
            }
        } else {
            when (sort) {
                PAYMENT_NAME -> list.sortedBy { it.name }
                PAYMENT_COST -> list.sortedBy { it.cost }
                PAYMENT_CATEGORY -> list.sortedBy { it.category }
                PAYMENT_DATE -> list.sortedBy { it.date }
                else -> list.sortedBy { it.id }
            }
        }
    }
}