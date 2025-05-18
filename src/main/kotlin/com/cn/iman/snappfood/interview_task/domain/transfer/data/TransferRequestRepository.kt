package com.cn.iman.snappfood.interview_task.domain.transfer.data

import com.cn.iman.snappfood.interview_task.application.arch.services.Repository
import jakarta.persistence.LockModeType
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.*

interface TransferRequestRepository : Repository<TransferRequestEntity> {

    /**
     * Lock this transfer request row so only one operator can process it at a time.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT r FROM TransferRequestEntity r WHERE r.id = :id")
    fun findByIdForUpdate(@Param("id") id: Long): Optional<TransferRequestEntity>
}
