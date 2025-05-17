package com.cn.iman.snappfood.interview_task.application.arch.controller.payload.request.search

import org.springframework.data.domain.Pageable
import org.springframework.data.domain.PageRequest as SpringPageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.domain.Specification

/**
 * Combines filters, sorts, and pagination into JPA-friendly structures.
 */
data class SearchRequest(
    val filters: FilterRequest? = null,
    val sorts: List<SortRequest>? = null,
    val page: PageRequest = PageRequest()
) {
    /**
     * Convert to JPA Specification for filtering.
     */
    fun <T> toSpecification(): Specification<T>? = filters?.toSpecification()

    /**
     * Convert to Spring Data Pageable for pagination and sorting.
     */
    fun toPageable(): Pageable {
        val sortObj = if (!sorts.isNullOrEmpty()) {
            Sort.by(
                sorts.map {
                    if (it.direction) Sort.Order.asc(it.key)
                    else Sort.Order.desc(it.key)
                }
            )
        } else {
            Sort.unsorted()
        }
        return SpringPageRequest.of(page.number, page.size, sortObj)
    }
}