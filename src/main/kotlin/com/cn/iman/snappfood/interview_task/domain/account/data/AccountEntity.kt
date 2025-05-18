package com.cn.iman.snappfood.interview_task.domain.account.data

import com.cn.iman.snappfood.interview_task.application.arch.entity.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Index
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint

@Entity
@Table(
    name = "accounts",
    uniqueConstraints = [UniqueConstraint(columnNames = ["sheba_number"])],
    indexes = [
        Index(name = "idx_account_sheba", columnList = "sheba_number")
    ]
)
data class AccountEntity(
    @Column(name = "sheba_number", nullable = false, unique = true)
    val shebaNumber: String,

    @Column(nullable = false)
    var balance: Long,

    @Column(nullable = false)
    var reserved: Long = 0L
) : BaseEntity()