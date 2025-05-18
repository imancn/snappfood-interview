package com.cn.iman.snappfood.interview_task.domain.transfer.service.external

import com.cn.iman.snappfood.interview_task.domain.account.data.AccountEntity

interface TransferAccountServiceInterface {
    fun getBySheba(sheba: String): AccountEntity
    fun lockBalance(accountId: Long, balance: Long): Boolean
    fun unlockBalance(accountId: Long, balance: Long): Boolean
    fun increaseBalance(accountId: Long, balance: Long): Boolean
    fun decreaseBalance(accountId: Long, balance: Long): Boolean
}