package com.cn.iman.snappfood.interview_task.application.arch.controller.payload.request.search

import jakarta.persistence.criteria.Predicate
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.PageRequest as SpringPageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.domain.Specification

data class SearchRequest(
    val filters: FilterRequest? = null,
    val sorts: List<SortRequest>? = null,
    val page: PageRequest = PageRequest()
) {
    fun <T> toSpecification(): Specification<T>? = filters?.toSpecification()

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

class FilterRequest(
    private val expressions: List<Expression>
) {
    fun <T> toSpecification(): Specification<T> = Specification { root, query, cb ->
        val predicates: Array<Predicate> = expressions.mapNotNull { expr ->
            when {
                expr.values.isEmpty() -> null
                expr.values.size == 1 -> cb.equal(root.get<Any>(expr.key), expr.values[0])
                else -> root.get<Any>(expr.key).`in`(expr.values)
            }
        }.toTypedArray()
        cb.and(*predicates)
    }
}

data class Expression(
    val key: String,
    val values: List<Any>
)

data class SortRequest(
    val key: String,
    val direction: Boolean // true = ascending, false = descending
)

data class PageRequest(
    val number: Int = 0,
    val size: Int = 20
)