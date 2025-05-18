package com.cn.iman.snappfood.interview_task.application.arch.controller

import com.cn.iman.snappfood.interview_task.application.arch.controller.payload.request.search.SearchRequest
import com.cn.iman.snappfood.interview_task.application.arch.controller.payload.response.MessageResponse
import com.cn.iman.snappfood.interview_task.application.arch.controller.payload.response.PageResponse
import com.cn.iman.snappfood.interview_task.application.arch.entity.BaseEntity
import com.cn.iman.snappfood.interview_task.application.arch.services.EntityService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.parameters.RequestBody as OasRequestBody
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

abstract class EntityViewController<S : EntityService<*, T>, T : BaseEntity> {
    abstract var service: S

    @Operation(
        summary = "Get an entity by ID",
        description = "Retrieve a single entity by its unique identifier."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Entity found and returned successfully",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = BaseEntity::class)
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
    @GetMapping("/{id}")
    open fun getEntity(
        @Parameter(
            description = "Unique identifier of the entity",
            required = true,
            schema = Schema(type = "integer", format = "int64")
        )
        @PathVariable id: Long
    ): ResponseEntity<T> {
        val entity = service.getById(id)
        return ResponseEntity.ok(entity)
    }

    @Operation(
        summary = "List entities",
        description = "Retrieve a list of entities, up to the specified count."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "List of entities returned successfully",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(type = "array", implementation = BaseEntity::class)
                )]
            )
        ]
    )
    @GetMapping
    open fun getAllEntities(
        @Parameter(
            description = "Maximum number of entities to return (default is 1000)",
            required = false,
            schema = Schema(type = "integer", defaultValue = "1000")
        )
        @RequestParam(required = false, defaultValue = "1000") count: Int
    ): ResponseEntity<List<T>> {
        return ResponseEntity.ok(service.findTop(count))
    }

    @Operation(
        summary = "Search entities with filters, sorting, and pagination",
        description = """
            Perform an advanced search over entities using filter expressions, multi-field sorting, and pagination. 
            When no request body is provided, returns the top 10 entities in a default page response.
        """
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Search results returned in paginated format",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = PageResponse::class)
                )]
            )
        ]
    )
    @PostMapping("/search")
    open fun getEntities(
        @OasRequestBody(
            description = "Optional search request detailing filters, sorts, and pagination",
            required = false,
            content = [Content(schema = Schema(implementation = SearchRequest::class))]
        )
        @RequestBody request: SearchRequest?
    ): ResponseEntity<*> {
        if (request != null) {
            val pageable = if (!request.sorts.isNullOrEmpty()) {
                val sort = Sort.by(
                    request.sorts.map {
                        if (it.direction) Sort.Order.asc(it.key) else Sort.Order.desc(it.key)
                    }
                )
                PageRequest.of(request.page.number, request.page.size, sort)
            } else {
                PageRequest.of(request.page.number, request.page.size)
            }
            val entities = service.find(request.filters?.toSpecification(), pageable)
            return ResponseEntity.ok(
                PageResponse(
                    content = entities.content,
                    pageNumber = request.page.number,
                    pageSize = request.page.size,
                    totalElements = entities.totalElements
                )
            )
        } else {
            return ResponseEntity.ok(
                PageResponse(
                    content = service.findTop(10),
                    pageNumber = 0,
                    pageSize = 10,
                    totalElements = service.findTop(10).size.toLong()
                )
            )
        }
    }
}
