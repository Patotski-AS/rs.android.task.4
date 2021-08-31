package com.example.android.expenses.ui.list

import androidx.lifecycle.*
import com.example.android.expenses.database.PaymentRepository
import com.example.android.expenses.model.Payment
import kotlinx.coroutines.launch


class ListViewModel(
    private val repository: PaymentRepository
) : ViewModel() {

    private val _payments: LiveData<List<Payment>?> = repository.allPayments.asLiveData()
    val payments: LiveData<List<Payment>?>
        get() = _payments

    fun addPayment(payment: Payment) = viewModelScope.launch {
        repository.insert(payment)
    }

    fun getPayment(id: Int) =
        viewModelScope.launch {
            repository.getPayment(id)
        }

    fun updatePayment(payment: Payment) = viewModelScope.launch {
        repository.updatePayment(payment)
    }

    fun deletePayment(payment: Payment) = viewModelScope.launch {
        repository.deletePayment(payment)
    }

    fun update() = viewModelScope.launch {
        repository.clear()
    }

}
