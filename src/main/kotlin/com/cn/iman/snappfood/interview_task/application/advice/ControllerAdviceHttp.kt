package com.cn.iman.snappfood.interview_task.application.advice

import com.cn.iman.snappfood.interview_task.application.arch.controller.payload.response.MessageResponse
import com.cn.iman.snappfood.interview_task.application.services.BundleService
import io.swagger.v3.oas.annotations.Hidden
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest

@Hidden
@RestControllerAdvice
class ControllerAdviceHttp(
    @Autowired private val bundleService: BundleService
) {
    private val logger = LoggerFactory.getLogger(javaClass.simpleName)

    @ExceptionHandler(value = [InvalidCredentialException::class])
    fun handleInvalidTokenException(
        ex: InvalidCredentialException,
        request: WebRequest
    ): ResponseEntity<MessageResponse> {
        logger.error(getMessage(ex))
        return ResponseEntity(
            bundleService.getMessageResponse(ex.key, *ex.args), ex.httpStatus
        )
    }

    @ExceptionHandler(value = [AccessDeniedException::class])
    fun handleAccessDeniedException(ex: AccessDeniedException, request: WebRequest): ResponseEntity<MessageResponse> {
        logger.error(getMessage(ex))
        return ResponseEntity(
            bundleService.getMessageResponse(ex.key, *ex.args), ex.httpStatus
        )
    }

    @ExceptionHandler(value = [InvalidInputException::class])
    fun handleInternalServerError(ex: InvalidInputException, request: WebRequest): ResponseEntity<MessageResponse> {
        logger.error(getMessage(ex))
        return ResponseEntity(
            bundleService.getMessageResponse(ex.key, *ex.args), ex.httpStatus
        )
    }

    @ExceptionHandler(value = [InternalServerError::class])
    fun handleInternalServerError(ex: InternalServerError, request: WebRequest): ResponseEntity<MessageResponse> {
        logger.error(getMessage(ex))
        return ResponseEntity(
            bundleService.getMessageResponse(ex.key, *ex.args), ex.httpStatus
        )
    }

    @ExceptionHandler(value = [NotFoundException::class])
    fun handleNotFoundException(ex: NotFoundException, request: WebRequest): ResponseEntity<MessageResponse> {
        logger.error(getMessage(ex))
        return ResponseEntity(
            bundleService.getMessageResponse(ex.key, *ex.args), ex.httpStatus
        )
    }

    @ExceptionHandler(value = [UnprocessableException::class])
    fun handleUnprocessableEntityException(
        ex: UnprocessableException,
        request: WebRequest
    ): ResponseEntity<MessageResponse> {
        logger.error(getMessage(ex))
        return ResponseEntity(
            bundleService.getMessageResponse(ex.key, *ex.args), ex.httpStatus
        )
    }

    private fun getMessage(ex: Exception) = ex.message + "\n" + ex.stackTraceToString()

}