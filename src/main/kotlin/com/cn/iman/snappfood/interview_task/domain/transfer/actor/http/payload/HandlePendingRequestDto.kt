package com.cn.iman.snappfood.interview_task.domain.transfer.actor.http.payload

import com.cn.iman.snappfood.interview_task.domain.transfer.data.TransferRequestEntity

data class HandlePendingRequestDto(
    val status: TransferRequestEntity.TransferStatus,
    val note: String? = null
)