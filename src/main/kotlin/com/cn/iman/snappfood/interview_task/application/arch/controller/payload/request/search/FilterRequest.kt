package com.cn.iman.snappfood.interview_task.application.arch.controller.payload.request.search

import jakarta.persistence.criteria.Predicate
import org.springframework.data.jpa.domain.Specification

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