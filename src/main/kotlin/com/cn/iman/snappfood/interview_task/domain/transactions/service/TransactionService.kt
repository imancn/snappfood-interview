package com.cn.iman.snappfood.interview_task.domain.transactions.service

import com.cn.iman.snappfood.interview_task.application.arch.services.EntityService
import com.cn.iman.snappfood.interview_task.domain.transactions.data.TransactionEntity
import com.cn.iman.snappfood.interview_task.domain.transactions.data.TransactionRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Service
class TransactionService(
    override var repository: TransactionRepository,
) : EntityService<TransactionRepository, TransactionEntity>() {

    /**
     * When a transfer is initiated, log a deduction (debit) in the user’s ledger.
     * We record this as a negative‐amount entry.
     */
    @Transactional(
        propagation = Propagation.MANDATORY,
        isolation = Isolation.READ_COMMITTED,
        timeout = 5
    )
    override fun recordDeduction(accountId: String, amount: Long) {
        val tx = TransactionEntity(
            fromAccountId = accountId,
            toAccountId = accountId,   // self‐transfer to show funds reserved
            amount = -amount
        )
        repository.save(tx)
    }

    /**
     * When a transfer is confirmed (or a cancellation return), log the actual
     * money movement between two accounts as a positive‐amount entry.
     */
    @Transactional(
        propagation = Propagation.MANDATORY,
        isolation = Isolation.READ_COMMITTED,
        timeout = 5
    )
    override fun recordPayment(from: String, to: String, amount: Long) {
        val tx = TransactionEntity(
            fromAccountId = from,
            toAccountId = to,
            amount = amount
        )
        repository.save(tx)
    }
}
