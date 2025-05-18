package com.cn.iman.snappfood.interview_task.domain.transfer.data

import com.cn.iman.snappfood.interview_task.application.arch.entity.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Index
import jakarta.persistence.Table

@Entity
@Table(
    name = "transfer_requests",
    indexes = [
        Index(name = "idx_req_from_sheba", columnList = "from_sheba_number"),
        Index(name = "idx_req_to_sheba", columnList = "to_sheba_number"),
        Index(name = "idx_req_status", columnList = "status")
    ]
)
data class TransferRequestEntity(
    @Column(name = "price", nullable = false)
    var price: Long,

    @Column(name = "from_sheba_number", nullable = false)
    var fromShebaNumber: String,

    @Column(name = "to_sheba_number", nullable = false)
    var toShebaNumber: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    var status: TransferStatus = TransferStatus.PENDING
) : BaseEntity() {
    enum class TransferStatus { PENDING, CONFIRMED, CANCELED }
}