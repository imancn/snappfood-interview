package com.cn.iman.snappfood.interview_task.application.arch.controller.payload.response

import com.cn.iman.snappfood.interview_task.application.advice.UnprocessableException
import org.springframework.data.domain.Page

data class PageResponse<T>(
    val content: List<T>,
    val pageNumber: Int,
    val pageSize: Int,
    val totalPages: Int,
    val totalElements: Long
) {
    constructor(
        content: List<T>,
        pageNumber: Int,
        pageSize: Int,
        totalElements: Long
    ) : this(
        content = content,
        pageNumber = pageNumber,
        pageSize = pageSize,
        totalPages = if (pageSize > 0) ((totalElements + pageSize - 1) / pageSize).toInt() else throw UnprocessableException(
            "pageSize must be bigger that 0"
        ),
        totalElements = totalElements
    )
}

fun <T> List<T>.paginate(pageSize: Int, pageNumber: Int): PageResponse<T> {
    if (pageSize <= 0)
        throw UnprocessableException("pageSize must be bigger that 0")
    val total = this.size
    val start = pageNumber * pageSize
    val end = minOf(start + pageSize, total)
    val content = if (start <= total) this.subList(start, end) else emptyList()
    val totalPages = (total + pageSize - 1) / pageSize

    return PageResponse(
        content = content,
        totalElements = total.toLong(),
        pageNumber = pageNumber,
        pageSize = pageSize,
        totalPages = totalPages
    )
}

fun <T> Page<T>.toCustomPage(): PageResponse<T> {
    return PageResponse(
        content = this.content,
        totalElements = this.totalElements,
        pageNumber = this.number,
        pageSize = this.size,
        totalPages = this.totalPages
    )
}