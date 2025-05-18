package com.cn.iman.snappfood.interview_task.application.advice

import com.cn.iman.snappfood.interview_task.application.arch.controller.payload.response.MessageResponse
import io.swagger.v3.oas.annotations.Hidden
import jakarta.validation.ValidationException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageConversionException
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest

@Hidden
@RestControllerAdvice
class ControllerAdvice {

    private val logger = LoggerFactory.getLogger(javaClass.simpleName)

    @ExceptionHandler(value = [MethodArgumentNotValidException::class])
    fun handleValidationException(
        ex: MethodArgumentNotValidException,
        request: WebRequest
    ): ResponseEntity<MessageResponse> {
        logger.error(getMessage(ex))
        return ResponseEntity(
            MessageResponse("invalid.input", ex.detailMessageArguments.toString()),
            HttpStatus.BAD_REQUEST
        )
    }

    @ExceptionHandler(value = [ValidationException::class])
    fun handleValidationException(ex: ValidationException, request: WebRequest): ResponseEntity<MessageResponse> {
        logger.error(getMessage(ex))
        return ResponseEntity(
            MessageResponse("invalid.input", ex.message ?: ""),
            HttpStatus.BAD_REQUEST
        )
    }

    @ExceptionHandler(value = [NoSuchElementException::class])
    fun handleNoSuchElementException(
        ex: NoSuchElementException,
        request: WebRequest
    ): ResponseEntity<MessageResponse> {
        logger.error(getMessage(ex))
        return ResponseEntity(
            MessageResponse("not.found", ex.message ?: ""),
            HttpStatus.NOT_FOUND
        )
    }

    // Todo: Remove after test
    @ExceptionHandler(value = [HttpMessageConversionException::class])
    fun handleHttpMessageConversionException(
        ex: HttpMessageNotReadableException,
        request: WebRequest
    ): ResponseEntity<MessageResponse> {
        logger.error(getMessage(ex))
        return ResponseEntity(
            MessageResponse("invalid.input", ex.message ?: ""),
            HttpStatus.BAD_REQUEST
        )
    }

    private fun getMessage(ex: Exception) = ex.message + "\n" + ex.stackTraceToString()
}
