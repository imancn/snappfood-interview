package com.cn.iman.snappfood.interview_task

import com.cn.iman.snappfood.interview_task.domain.account.data.AccountEntity
import com.cn.iman.snappfood.interview_task.domain.account.data.AccountRepository
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class DataGenerator(
    private val accountRepository: AccountRepository
) {
    @PostConstruct
    @Transactional
    fun generateAccounts() {
        // Only seed when no accounts are present
        if (accountRepository.count() > 0) return

        val initialAccounts = listOf(
            AccountEntity(
                sheba = "IR000000000000000000000001",
                balance = 1_000_000L
            ),
            AccountEntity(
                sheba = "IR000000000000000000000002",
                balance = 2_000_000L
            ),
            AccountEntity(
                sheba = "IR000000000000000000000003",
                balance = 5_000_000L
            )
        )
        accountRepository.saveAll(initialAccounts)
    }
}