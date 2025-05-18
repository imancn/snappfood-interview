package com.cn.iman.snappfood.interview_task.domain.transfer.actor.http.payload

data class ShebaRequestDto(
    val price: Long,
    val fromShebaNumber: String,
    val toShebaNumber: String,
    val note: String? = null
)

