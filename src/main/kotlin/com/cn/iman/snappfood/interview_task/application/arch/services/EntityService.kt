package com.cn.iman.snappfood.interview_task.application.arch.services

import com.cn.iman.snappfood.interview_task.application.arch.controller.payload.response.PageResponse
import com.cn.iman.snappfood.interview_task.application.arch.controller.payload.response.toCustomPage
import com.cn.iman.snappfood.interview_task.application.arch.entity.BaseEntity
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Service

@Service
abstract class EntityService<R, T>
        where R : JpaRepository<T, Long>,
              R : JpaSpecificationExecutor<T>,
              T : BaseEntity {

    protected abstract var repository: R
    private val logger = LoggerFactory.getLogger(javaClass.simpleName)

    fun save(entity: T): T = repository.save(entity)

    fun create(entity: T): T = save(entity.apply { id = null })

    fun update(entity: T): T {
        getById(entity.id())
        return repository.save(entity)
    }

    fun getById(id: Long): T = repository.findById(id)
        .orElseThrow { NoSuchElementException("Entity with id $id not found") }

    fun tryById(id: Long): T? = repository.findById(id).orElse(null)

    fun findTop(count: Int): List<T> = repository.findAll(
        org.springframework.data.domain.PageRequest.of(
            0, count,
            org.springframework.data.domain.Sort.by(
                org.springframework.data.domain.Sort.Direction.DESC, "id"
            )
        )
    ).content

    fun delete(id: Long): Boolean {
        return try {
            repository.deleteById(id)
            true
        } catch (ex: Exception) {
            logger.error(ex.message + "\n" + ex.stackTraceToString())
            false
        }
    }

    /**
     * Executes a dynamic search by Specification and pagination.
     */
    fun find(
        spec: Specification<T>?,
        pageable: Pageable
    ): PageResponse<T> = if (spec != null) repository.findAll(spec, pageable).toCustomPage()
    else repository.findAll(pageable).toCustomPage()
}
