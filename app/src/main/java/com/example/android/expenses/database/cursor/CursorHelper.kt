package com.example.android.expenses.database.cursor

import android.app.Application
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.example.android.expenses.*
import com.example.android.expenses.model.Payment
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class CursorHelper(
    private val application: Application,
    private val dbHelper: PaymentDBHelper = PaymentDBHelper(application)
) {

    fun getPaymentsList(filter: String?): Flow<List<Payment>> {
        return flowOf(dbHelper.getPaymentsList(filter))
    }

    fun insert(payment: Payment) {
        val contentValues = ContentValues()
        contentValues.put(PAYMENT_NAME, payment.name)
        contentValues.put(PAYMENT_COST, payment.cost)
        contentValues.put(PAYMENT_CATEGORY, payment.category)
        contentValues.put(PAYMENT_DATE, payment.date.time)
        dbHelper.readableDatabase.insert(DB_PAYMENTS_NAME, null, contentValues)
    }

    fun getPayment(id: Int): Flow<Payment?> {
        return flowOf(dbHelper.getPayment(id))
    }

    fun delete(payment: Payment) {
        val db: SQLiteDatabase = application.applicationContext.openOrCreateDatabase(
            DB_PAYMENTS_NAME, Context.MODE_PRIVATE, null
        )
        try {
            db.delete(DB_PAYMENTS_NAME, "$ID = ${payment.id}", null)
        } catch (e: Exception) {

        } finally {
            db.close()
        }
    }

    fun update(payment: Payment) {
        val db: SQLiteDatabase = application.applicationContext.openOrCreateDatabase(
            DB_PAYMENTS_NAME,
            Context.MODE_PRIVATE,
            null
        )
        try {
            db.execSQL(
                "UPDATE $DB_PAYMENTS_NAME SET " +
                        "$PAYMENT_NAME = '${payment.name}', " +
                        "$PAYMENT_COST = '${payment.cost}', " +
                        "$PAYMENT_CATEGORY = '${payment.category}', " +
                        "$PAYMENT_DATE = '${payment.date.time}' " +
                        "WHERE $ID = '${payment.id}'"
            )
        } catch (e: Exception) {

        } finally {
            db.close()
        }

    }

}