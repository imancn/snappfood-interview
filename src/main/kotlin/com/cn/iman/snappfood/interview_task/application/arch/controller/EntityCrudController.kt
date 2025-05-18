package com.cn.iman.snappfood.interview_task.application.arch.controller

import com.cn.iman.snappfood.interview_task.application.arch.controller.payload.response.MessageResponse
import com.cn.iman.snappfood.interview_task.application.arch.entity.BaseEntity
import com.cn.iman.snappfood.interview_task.application.arch.services.EntityService
import com.cn.iman.snappfood.interview_task.application.services.BundleService
import io.swagger.v3.oas.annotations.Operation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

abstract class EntityCrudController<S : EntityService<*, T>, T : BaseEntity> : EntityViewController<S, T>() {

    @Autowired
    private lateinit var bundle: BundleService

    @Operation(
        summary = "Create a new entity",
        description = "Creates a new entity. Requires ADMIN role."
    )
    @PostMapping("/create")
    open fun create(@RequestBody dto: T): ResponseEntity<T> {
        return ResponseEntity.ok(service.create(dto))
    }

    @Operation(
        summary = "Update an existing entity",
        description = "Updates an existing entity. Requires ADMIN role."
    )
    @PutMapping("/update")
    open fun update(@RequestBody dto: T): ResponseEntity<T> {
        return ResponseEntity.ok(service.update(dto))
    }

    @Operation(
        summary = "Archive an entity",
        description = "Archives an entity by ID. Requires ADMIN role."
    )
    @DeleteMapping("delete/{id}")
    open fun delete(@PathVariable id: Long): ResponseEntity<MessageResponse> {
        return ResponseEntity.ok(
            bundle.getMessageResponse(
                if (service.delete(id)) "successful" else "failed"
            )
        )
    }
}
