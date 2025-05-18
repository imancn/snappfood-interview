package com.cn.iman.snappfood.interview_task.domain.transfer.actor.http.payload

data class ShebaResponse<T>(
    val message: String,
    val request: T
)