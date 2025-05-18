package com.cn.iman.snappfood.interview_task.domain.account.service

import com.cn.iman.snappfood.interview_task.application.advice.NotFoundException
import com.cn.iman.snappfood.interview_task.application.arch.services.EntityService
import com.cn.iman.snappfood.interview_task.domain.account.data.AccountEntity
import com.cn.iman.snappfood.interview_task.domain.account.data.AccountRepository
import com.cn.iman.snappfood.interview_task.domain.transfer.service.external.TransferAccountServiceInterface
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.util.regex.Pattern

@Service
class AccountService(
    override var repository: AccountRepository,
) : EntityService<AccountRepository, AccountEntity>(), TransferAccountServiceInterface {

    companion object {
        private val IBAN_PATTERN = Pattern.compile("^IR\\d{24}$")

        /** True if it’s an IR‐prefixed 24‐digit IBAN. */
        fun isValidIban(iban: String): Boolean =
            IBAN_PATTERN.matcher(iban).matches()
    }

    override fun getBySheba(sheba: String): AccountEntity {
        return repository.findByShebaNumber(sheba)
            ?: throw NotFoundException("Entity with sheba number $sheba not found")
    }

    /**
     * Reserve funds: deduct from available balance, increase reserved.
     */
    @Transactional(
        propagation = Propagation.REQUIRED,
        isolation = Isolation.READ_COMMITTED,
        timeout = 5
    )
    override fun lockBalance(accountId: Long, balance: Long): Boolean {
        val acc = repository.findByIdForUpdate(accountId)
        if (acc.balance < balance) return false
        acc.balance -= balance
        acc.reserved += balance
        repository.save(acc)
        return true
    }

    /**
     * On cancellation: return reserved funds to available.
     */
    @Transactional(
        propagation = Propagation.REQUIRED,
        isolation = Isolation.READ_COMMITTED,
        timeout = 5
    )
    override fun unlockBalance(accountId: Long, balance: Long): Boolean {
        val acc = repository.findByIdForUpdate(accountId)
        if (acc.reserved < balance) return false
        acc.reserved -= balance
        acc.balance += balance
        repository.save(acc)
        return true
    }

    /**
     * Deposit into account.
     */
    @Transactional(
        propagation = Propagation.MANDATORY,
        isolation = Isolation.READ_COMMITTED,
        timeout = 5
    )
    override fun increaseBalance(accountId: Long, balance: Long): Boolean {
        val acc = repository.findByIdForUpdate(accountId)
        acc.balance += balance
        repository.save(acc)
        return true
    }

    /**
     * Finalize deduction of reserved funds (on confirmation).
     */
    @Transactional(
        propagation = Propagation.MANDATORY,
        isolation = Isolation.READ_COMMITTED,
        timeout = 5
    )
    override fun decreaseBalance(accountId: Long, balance: Long): Boolean {
        val acc = repository.findByIdForUpdate(accountId)
        if (acc.reserved < balance) return false
        acc.reserved -= balance
        repository.save(acc)
        return true
    }
}
