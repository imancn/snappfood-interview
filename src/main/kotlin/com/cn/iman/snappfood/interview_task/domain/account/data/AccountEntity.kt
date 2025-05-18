package com.cn.iman.snappfood.interview_task.domain.account.data

import com.cn.iman.snappfood.interview_task.application.arch.entity.BaseEntity
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = "accounts")
data class AccountEntity(
    val sheba: String,
    var balance: Long,
    var reserved: Long = 0L
) : BaseEntity()