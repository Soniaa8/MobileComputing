package com.codemave.mobilecomputing.data.repository

import com.codemave.mobilecomputing.data.entity.Payment
import com.codemave.mobilecomputing.data.room.PaymentDao
import com.codemave.mobilecomputing.data.room.PaymentToCategory
import kotlinx.coroutines.flow.Flow

/**
 * A data repository for [Payment] instances
 */
class PaymentRepository(
    private val paymentDao: PaymentDao
) {
    /**
     * Returns a flow containing the list of payments associated with the category with the
     * given [categoryId]
     */
    fun paymentsInCategory(categoryId: Long) : Flow<List<PaymentToCategory>> {
        return paymentDao.paymentsFromCategory(categoryId)
    }

    /**
     * Add a new [Payment] to the payment store
     */
    suspend fun addPayment(payment: Payment) = paymentDao.insert(payment)

    /**
     * Update [Payment] to the payment store
     */
    suspend fun updatePayment(payment: Payment) = paymentDao.update(payment)

    /**
     * Delete [Payment] to the payment store
     */
    suspend fun deletePayment(payment: Payment) = paymentDao.delete(payment)

    suspend fun getPaymentWithId(paymentId: Long): Payment? = paymentDao.getPaymentWithId(paymentId)

}