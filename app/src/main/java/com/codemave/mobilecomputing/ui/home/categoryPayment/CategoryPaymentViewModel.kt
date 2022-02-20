package com.codemave.mobilecomputing.ui.home.categoryPayment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codemave.mobilecomputing.Graph
import com.codemave.mobilecomputing.data.repository.PaymentRepository
import com.codemave.mobilecomputing.data.room.PaymentToCategory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.*


class CategoryPaymentViewModel(
    private val categoryId: Long,
    private val paymentRepository: PaymentRepository = Graph.paymentRepository

) : ViewModel() {
    private val _state = MutableStateFlow(CategoryPaymentViewState())

    val state: StateFlow<CategoryPaymentViewState>
        get() = _state

    init {
        viewModelScope.launch {
            paymentRepository.paymentsInCategory(categoryId).collect { list ->
                val beforeTimeReminder= list.filter {
                    if (it.payment.paymentDate <= Date().time) {
                        paymentRepository.updatePayment(it.payment.copy(paymentActive = true))
                    }
                    it.payment.paymentActive
                }

                _state.value = CategoryPaymentViewState(
                    payments = beforeTimeReminder
                )
            }
        }
    }
}

data class CategoryPaymentViewState(
    val payments: List<PaymentToCategory> = emptyList()
)