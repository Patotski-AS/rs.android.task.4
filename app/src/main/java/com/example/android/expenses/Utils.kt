package com.example.android.expenses

import com.example.android.expenses.model.Payment


fun categories(): List<String> {
    return listOf("other", "Food", "Clothes", "hobby", "auto", "Repair")
}

fun startPayment() : List<Payment>{
    return (0..4)
        .map { Payment("name$it", (10..100).random().toDouble(),
            categories()[(categories().indices).random()]
        ) }
        .toList()
}