package com.cn.iman.snappfood.interview_task.domain.transactions.data

import com.cn.iman.snappfood.interview_task.application.arch.entity.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Index
import jakarta.persistence.Table

@Entity
@Table(
    name = "transactions",
    indexes = [
        Index(name = "idx_tx_from_account", columnList = "from_account_id"),
        Index(name = "idx_tx_to_account", columnList = "to_account_id")
    ]
)
data class TransactionEntity(
    @Column(name = "from_account_id", nullable = false)
    val fromAccountId: Long,

    @Column(name = "to_account_id", nullable = false)
    val toAccountId: Long,

    @Column(nullable = false)
    val amount: Long
) : BaseEntity()