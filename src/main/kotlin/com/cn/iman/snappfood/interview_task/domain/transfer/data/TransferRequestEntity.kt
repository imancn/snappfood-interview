package com.cn.iman.snappfood.interview_task.domain.transfer.data

import com.cn.iman.snappfood.interview_task.application.arch.entity.BaseEntity
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Table

@Entity
@Table(name = "transfer_requests")
data class TransferRequestEntity(
    var price: Long,
    var fromShebaNumber: String,
    var toShebaNumber: String,
    @Enumerated(EnumType.STRING)
    var status: TransferStatus = TransferStatus.PENDING,
) : BaseEntity() {
    enum class TransferStatus { PENDING, CONFIRMED, CANCELED }
}