package com.cn.iman.snappfood.interview_task.domain.transfer.service.external

interface TransferTransactionServiceInterface {
    fun recordDeduction(accountId: Long, amount: Long)
    fun recordPayment(from: Long, to: Long, amount: Long)
}
