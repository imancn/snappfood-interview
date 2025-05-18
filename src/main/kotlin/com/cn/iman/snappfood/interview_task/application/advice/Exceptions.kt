package com.cn.iman.snappfood.interview_task.application.advice

import org.springframework.http.HttpStatus

sealed class HttpException(val httpStatus: HttpStatus, val key: String, val args: Array<String>) : RuntimeException()

class InvalidCredentialException(key: String = "invalid.credentials", vararg args: Any) :
    HttpException(HttpStatus.UNAUTHORIZED, key, args.map { it.toString() }.toTypedArray())

class AccessDeniedException(key: String = "access.denied", vararg args: Any) :
    HttpException(HttpStatus.FORBIDDEN, key, args.map { it.toString() }.toTypedArray())

class InvalidInputException(key: String = "invalid.input", vararg args: Any) :
    HttpException(HttpStatus.BAD_REQUEST, key, args.map { it.toString() }.toTypedArray())

class UnprocessableException(key: String = "unprocessable", vararg args: Any) :
    HttpException(HttpStatus.UNPROCESSABLE_ENTITY, key, args.map { it.toString() }.toTypedArray())

class InternalServerError(key: String = "internal.server.error", vararg args: Any) :
    HttpException(HttpStatus.INTERNAL_SERVER_ERROR, key, args.map { it.toString() }.toTypedArray())

class NotFoundException(key: String = "not.found", vararg args: Any) :
    HttpException(HttpStatus.NOT_FOUND, key, args.map { it.toString() }.toTypedArray())