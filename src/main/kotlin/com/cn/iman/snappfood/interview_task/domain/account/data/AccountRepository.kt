package com.cn.iman.snappfood.interview_task.domain.account.data

import com.cn.iman.snappfood.interview_task.application.arch.services.Repository
import jakarta.persistence.LockModeType
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface AccountRepository : Repository<AccountEntity> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM AccountEntity a WHERE a.id = :accountId")
    fun findByIdForUpdate(@Param("accountId") accountId: Long): AccountEntity

    fun findBySheba(sheba: String): AccountEntity?
}
