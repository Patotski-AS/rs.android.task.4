package com.example.android.expenses.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.android.expenses.database.*
import java.util.*

@Entity(tableName = "payments")
data class Payment(
    @ColumnInfo(name = PAYMENT_NAME) var name:String?,
    @ColumnInfo(name = PAYMENT_COST) var cost: Double?,
    @ColumnInfo(name = PAYMENT_CATEGORY) var category: String?,
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    @ColumnInfo(name = PAYMENT_DATE) val date: Date = Date()
)
