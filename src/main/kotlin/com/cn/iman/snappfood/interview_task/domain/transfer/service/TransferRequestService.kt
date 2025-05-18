package com.cn.iman.snappfood.interview_task.domain.transfer.service

import com.cn.iman.snappfood.interview_task.application.advice.InvalidInputException
import com.cn.iman.snappfood.interview_task.application.advice.UnprocessableException
import com.cn.iman.snappfood.interview_task.application.arch.services.EntityService
import com.cn.iman.snappfood.interview_task.domain.account.service.AccountService
import com.cn.iman.snappfood.interview_task.domain.transfer.actor.http.payload.HandlePendingRequestDto
import com.cn.iman.snappfood.interview_task.domain.transfer.actor.http.payload.ShebaRequestDto
import com.cn.iman.snappfood.interview_task.domain.transfer.actor.http.payload.ShebaResponse
import com.cn.iman.snappfood.interview_task.domain.transfer.actor.http.payload.TransferRequestResponseDto
import com.cn.iman.snappfood.interview_task.domain.transfer.data.TransferRequestEntity
import com.cn.iman.snappfood.interview_task.domain.transfer.data.TransferRequestRepository
import com.cn.iman.snappfood.interview_task.domain.transfer.service.external.TransferAccountServiceInterface
import com.cn.iman.snappfood.interview_task.domain.transfer.service.external.TransferTransactionServiceInterface
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Service
class TransferRequestService(
    override var repository: TransferRequestRepository,
    private val transferAccountServiceInterface: TransferAccountServiceInterface,
    private val transferTransactionServiceInterface: TransferTransactionServiceInterface,
) : EntityService<TransferRequestRepository, TransferRequestEntity>() {

    @Transactional(
        propagation = Propagation.REQUIRED,
        isolation = Isolation.READ_COMMITTED,
        timeout = 60
    )
    fun createPayaPendingRequest(dto: ShebaRequestDto): ShebaResponse<TransferRequestResponseDto> {
        if (!AccountService.isValidIban(dto.fromShebaNumber) || !AccountService.isValidIban(dto.toShebaNumber)) {
            throw InvalidInputException("invalid.input", "Invalid IBAN format")
        }
        val fromAccount = transferAccountServiceInterface.getBySheba(dto.fromShebaNumber)
        run { transferAccountServiceInterface.getBySheba(dto.fromShebaNumber) }
        val locked = transferAccountServiceInterface.lockBalance(fromAccount.id(), dto.price)
        if (!locked) {
            throw UnprocessableException("insufficient.funds")
        }
        transferTransactionServiceInterface.recordDeduction(
            dto.fromShebaNumber,
            dto.price
        )
        val requestEntity = TransferRequestEntity(
            price = dto.price,
            fromShebaNumber = dto.fromShebaNumber,
            toShebaNumber = dto.toShebaNumber
        )
        val savedEntity = repository.save(requestEntity)
        return ShebaResponse(
            message = "Request is saved successfully and is in pending status",
            request = TransferRequestResponseDto.fromEntity(savedEntity)
        )
    }

    @Transactional(
        propagation = Propagation.REQUIRED,
        isolation = Isolation.READ_COMMITTED,
        timeout = 60,
        rollbackFor = [Exception::class]
    )
    fun handlePendingRequest(dto: HandlePendingRequestDto, id: Long): ShebaResponse<TransferRequestResponseDto> {
        val request = repository.findByIdForUpdate(id).orElseThrow { InvalidInputException("Request not found") }
        if (request.status != TransferRequestEntity.TransferStatus.PENDING) {
            throw UnprocessableException("request.already.handled", id.toString())
        }
        when (dto.status) {
            TransferRequestEntity.TransferStatus.CONFIRMED -> {
                val fromAccount = transferAccountServiceInterface.getBySheba(request.fromShebaNumber)
                transferAccountServiceInterface.decreaseBalance(
                    fromAccount.id(),
                    request.price
                )

                val toAccount = transferAccountServiceInterface.getBySheba(request.toShebaNumber)
                transferAccountServiceInterface.increaseBalance(
                    toAccount.id(),
                    request.price
                )

                transferTransactionServiceInterface.recordPayment(
                    request.fromShebaNumber,
                    request.toShebaNumber,
                    request.price
                )

                request.status = TransferRequestEntity.TransferStatus.CONFIRMED
            }

            TransferRequestEntity.TransferStatus.CANCELED -> {
                val fromAccount = transferAccountServiceInterface.getBySheba(request.fromShebaNumber)
                transferAccountServiceInterface.unlockBalance(
                    fromAccount.id(),
                    request.price
                )

                transferTransactionServiceInterface.recordPayment(
                    request.toShebaNumber,
                    request.fromShebaNumber,
                    request.price
                )

                request.status = TransferRequestEntity.TransferStatus.CANCELED
            }

            TransferRequestEntity.TransferStatus.PENDING ->
                throw UnprocessableException("You cannot change request status to PENDING")
        }

        val updatedRequest = repository.save(request)
        val confirmed = dto.status == TransferRequestEntity.TransferStatus.CONFIRMED
        return ShebaResponse(
            message = if (confirmed) "Request is Confirmed!" else "Request is Canceled",
            request = TransferRequestResponseDto.fromEntity(updatedRequest)
        )
    }
}
