package com.cn.iman.snappfood.interview_task.domain.transactions.service

import com.cn.iman.snappfood.interview_task.application.arch.services.EntityService
import com.cn.iman.snappfood.interview_task.domain.transactions.data.TransactionEntity
import com.cn.iman.snappfood.interview_task.domain.transactions.data.TransactionRepository
import com.cn.iman.snappfood.interview_task.domain.transfer.service.external.TransferTransactionServiceInterface
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Service
class TransactionService(
    override var repository: TransactionRepository,
) : EntityService<TransactionRepository, TransactionEntity>(), TransferTransactionServiceInterface {

    /**
     * When a transfer is initiated, log a deduction (debit) in the user’s ledger.
     * We record this as a negative‐amount entry.
     */
    @Transactional(
        propagation = Propagation.REQUIRED,
        isolation = Isolation.READ_COMMITTED,
        timeout = 5
    )
    override fun recordDeduction(accountId: Long, amount: Long) {
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
        propagation = Propagation.REQUIRED,
        isolation = Isolation.READ_COMMITTED,
        timeout = 5
    )
    override fun recordPayment(from: Long, to: Long, amount: Long) {
        val tx = TransactionEntity(
            fromAccountId = from,
            toAccountId = to,
            amount = amount
        )
        repository.save(tx)
    }
}
