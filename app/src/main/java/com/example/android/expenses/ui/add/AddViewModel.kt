package com.example.android.expenses.ui.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.expenses.database.PaymentRepository
import com.example.android.expenses.model.Payment
import kotlinx.coroutines.launch

class AddViewModel(
    private val repository: PaymentRepository,
) : ViewModel() {

    var payment = Payment("no name",0.0,null)

    fun addPayment() = viewModelScope.launch {
        repository.insert(payment)
    }

    fun updatePayment(payment: Payment) = viewModelScope.launch {
        repository.updatePayment(payment)
    }

}