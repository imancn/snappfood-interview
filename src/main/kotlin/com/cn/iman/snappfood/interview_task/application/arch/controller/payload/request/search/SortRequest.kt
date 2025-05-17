package com.cn.iman.snappfood.interview_task.application.arch.controller.payload.request.search

data class SortRequest(
    val key: String,
    val direction: Boolean // true = ascending, false = descending
)