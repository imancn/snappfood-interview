package com.cn.iman.snappfood.interview_task.domain.transfer.actor.http

import com.cn.iman.snappfood.interview_task.application.arch.controller.EntityViewController
import com.cn.iman.snappfood.interview_task.domain.transfer.actor.http.payload.HandlePendingRequestDto
import com.cn.iman.snappfood.interview_task.domain.transfer.actor.http.payload.ShebaRequestDto
import com.cn.iman.snappfood.interview_task.domain.transfer.actor.http.payload.ShebaResponse
import com.cn.iman.snappfood.interview_task.domain.transfer.actor.http.payload.TransferRequestResponseDto
import com.cn.iman.snappfood.interview_task.domain.transfer.data.TransferRequestEntity
import com.cn.iman.snappfood.interview_task.domain.transfer.service.TransferRequestService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.parameters.RequestBody as OasRequestBody
import io.swagger.v3.oas.annotations.Parameter
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/sheba")
@Tag(
    name = "Sheba Transfer",
    description = "Endpoints for initiating and processing Sheba (IBAN) transfer requests"
)
class PayaTransferRequestController(
    override var service: TransferRequestService,
) : EntityViewController<TransferRequestService, TransferRequestEntity>() {

    @PostMapping
    @Operation(
        summary = "Initiate a Sheba transfer request",
        description = "Subtracts the amount from the sender’s account, reserves it, logs a deduction transaction, and creates a pending transfer request."
        ,
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Request is saved successfully and is in pending status",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = ShebaResponse::class)
                )]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Invalid input (e.g. bad IBAN format)",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = ShebaResponse::class)
                )]
            ),
            ApiResponse(
                responseCode = "422",
                description = "Unprocessable Entity (e.g. insufficient funds, account not found)",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = ShebaResponse::class)
                )]
            )
        ]
    )
    fun sendShebaRequest(
        @OasRequestBody(
            description = "Payload for creating a new Sheba transfer request",
            required = true,
            content = [Content(schema = Schema(implementation = ShebaRequestDto::class))]
        )
        @RequestBody dto: ShebaRequestDto
    ): ResponseEntity<ShebaResponse<TransferRequestResponseDto>> {
        return ResponseEntity.ok(service.createPayaPendingRequest(dto))
    }

    @PutMapping("/{id}")
    @Operation(
        summary = "Confirm or cancel a pending transfer request",
        description = "Operator reviews a pending request and either confirms (executes payment) or cancels (refunds) it."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Request has been confirmed or canceled successfully",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = ShebaResponse::class)
                )]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Invalid input (e.g. malformed status or note)",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = ShebaResponse::class)
                )]
            ),
            ApiResponse(
                responseCode = "404",
                description = "Request not found",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = ShebaResponse::class)
                )]
            ),
            ApiResponse(
                responseCode = "422",
                description = "Unprocessable Entity (e.g. already processed)",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = ShebaResponse::class)
                )]
            )
        ]
    )
    fun handlePendingRequest(
        @Parameter(
            description = "ID of the transfer request to process",
            required = true,
            schema = Schema(type = "integer", format = "int64")
        )
        @PathVariable id: Long,

        @OasRequestBody(
            description = "Action to perform on the pending request (confirm or cancel)",
            required = true,
            content = [Content(schema = Schema(implementation = HandlePendingRequestDto::class))]
        )
        @RequestBody dto: HandlePendingRequestDto
    ): ResponseEntity<ShebaResponse<TransferRequestResponseDto>> {
        return ResponseEntity.ok(service.handlePendingRequest(dto, id))
    }
}
