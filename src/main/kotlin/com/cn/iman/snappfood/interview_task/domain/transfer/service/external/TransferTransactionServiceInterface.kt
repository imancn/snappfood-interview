package com.cn.iman.snappfood.interview_task.domain.transfer.service.external

interface TransferTransactionServiceInterface {
    fun recordDeduction(accountId: String, amount: Long)
    fun recordPayment(from: String, to: String, amount: Long)
}
