package com.example.android.expenses.ui.list.adapter

import com.example.android.expenses.model.Payment

interface ListListener {
    fun deleteItem(payment: Payment)

}