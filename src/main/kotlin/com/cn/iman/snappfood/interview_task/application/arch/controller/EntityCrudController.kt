package com.cn.iman.snappfood.interview_task.application.arch.controller

import com.cn.iman.snappfood.interview_task.application.arch.controller.payload.response.MessageResponse
import com.cn.iman.snappfood.interview_task.application.arch.entity.BaseEntity
import com.cn.iman.snappfood.interview_task.application.arch.services.EntityService
import com.cn.iman.snappfood.interview_task.application.services.BundleService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.parameters.RequestBody as OasRequestBody
import io.swagger.v3.oas.annotations.Parameter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * Generic CRUD controller providing create, update, and delete endpoints
 * for any entity type extending BaseEntity.
 */
@RestController
@RequestMapping
@Tag(name = "CRUD Operations", description = "Create, update, and delete operations for entities")
abstract class EntityCrudController<S : EntityService<*, T>, T : BaseEntity> : EntityViewController<S, T>() {

    @Autowired
    private lateinit var bundle: BundleService

    @PostMapping("/create")
    @Operation(
        summary = "Create a new entity",
        description = "Creates a new entity record of type ${'$'}{T::class.simpleName} and returns the created object."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Entity created successfully",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = BaseEntity::class)
                )]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Invalid input data",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = MessageResponse::class)
                )]
            )
        ]
    )
    open fun create(
        @OasRequestBody(
            description = "Entity payload to create",
            required = true,
            content = [Content(schema = Schema(implementation = BaseEntity::class))]
        )
        @RequestBody dto: T
    ): ResponseEntity<T> {
        return ResponseEntity.ok(service.create(dto))
    }

    @PutMapping("/update")
    @Operation(
        summary = "Update an existing entity",
        description = "Updates an existing entity record of type ${'$'}{T::class.simpleName} and returns the updated object."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Entity updated successfully",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = BaseEntity::class)
                )]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Invalid input data",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = MessageResponse::class)
                )]
            ),
            ApiResponse(
                responseCode = "404",
                description = "Entity not found",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = MessageResponse::class)
                )]
            )
        ]
    )
    open fun update(
        @OasRequestBody(
            description = "Entity payload to update",
            required = true,
            content = [Content(schema = Schema(implementation = BaseEntity::class))]
        )
        @RequestBody dto: T
    ): ResponseEntity<T> {
        return ResponseEntity.ok(service.update(dto))
    }

    @DeleteMapping("/delete/{id}")
    @Operation(
        summary = "Delete an entity by ID",
        description = "Deletes the entity record of type ${'$'}{T::class.simpleName} with the given ID and returns a message response."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Entity deleted successfully",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = MessageResponse::class)
                )]
            ),
            ApiResponse(
                responseCode = "404",
                description = "Entity not found",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = MessageResponse::class)
                )]
            )
        ]
    )
    open fun delete(
        @Parameter(
            description = "ID of the entity to delete",
            required = true,
            schema = Schema(type = "integer", format = "int64")
        )
        @PathVariable id: Long
    ): ResponseEntity<MessageResponse> {
        return ResponseEntity.ok(
            bundle.getMessageResponse(
                if (service.delete(id)) "successful" else "failed"
            )
        )
    }
}
