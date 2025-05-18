package com.cn.iman.snappfood.interview_task.domain.transfer.actor.http

import com.cn.iman.snappfood.interview_task.application.arch.controller.EntityViewController
import com.cn.iman.snappfood.interview_task.domain.transfer.actor.http.payload.HandlePendingRequestDto
import com.cn.iman.snappfood.interview_task.domain.transfer.actor.http.payload.ShebaRequestDto
import com.cn.iman.snappfood.interview_task.domain.transfer.actor.http.payload.ShebaResponse
import com.cn.iman.snappfood.interview_task.domain.transfer.actor.http.payload.TransferRequestResponseDto
import com.cn.iman.snappfood.interview_task.domain.transfer.data.TransferRequestEntity
import com.cn.iman.snappfood.interview_task.domain.transfer.service.TransferRequestService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(path = ["/api/sheba"])
//@RequestMapping(path = ["api/transfer-request/paya"])
class PayaTransferRequestController(
    override var service: TransferRequestService,
) : EntityViewController<TransferRequestService, TransferRequestEntity>() {

    @PostMapping
    fun sendShebaRequest(
        @RequestBody dto: ShebaRequestDto
    ): ResponseEntity<ShebaResponse<TransferRequestResponseDto>> {
        return ResponseEntity.ok(service.createPayaPendingRequest(dto))
    }

    @PutMapping("/{id}")
    fun handlePendingRequest(
        @PathVariable(required = false) id: Long,
        @RequestBody dto: HandlePendingRequestDto
    ): ResponseEntity<ShebaResponse<TransferRequestResponseDto>> {
        return ResponseEntity.ok(service.handlePendingRequest(dto, id))
    }
}
