package com.cn.iman.snappfood.interview_task.domain.transactions.data

import com.cn.iman.snappfood.interview_task.application.arch.entity.BaseEntity
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = "transactions")
data class TransactionEntity(
    val fromAccountId: Long,
    val toAccountId: Long,
    val amount: Long
) : BaseEntity()