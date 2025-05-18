package com.cn.iman.snappfood.interview_task.domain.transfer.actor.http.payload

import com.cn.iman.snappfood.interview_task.domain.transfer.data.TransferRequestEntity
import java.util.*

data class TransferRequestResponseDto(
    val id: Long,
    val price: Long,
    val status: TransferRequestEntity.TransferStatus,
    val fromShebaNumber: String,
    val toShebaNumber: String,
    val createdAt: Date?
) {
    companion object {
        fun fromEntity(e: TransferRequestEntity) = TransferRequestResponseDto(
            id = e.id(),
            price = e.price,
            status = e.status,
            fromShebaNumber = e.fromShebaNumber,
            toShebaNumber = e.toShebaNumber,
            createdAt = e.createdAt
        )
    }
}