package com.example.android.expenses.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "payments")
data class Payment(
    @ColumnInfo(name = "name") var name:String?,
    @ColumnInfo(name = "cost") var cost: Double?,
    @ColumnInfo(name = "category") var category: String?,
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    @ColumnInfo(name = "date") val date: Date = Date()
)

//DateFormat.format("dd.MM.yyyy, HH:mm", Date()).toString()
