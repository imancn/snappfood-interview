package com.cn.iman.snappfood.interview_task.domain.account.actor.http

import com.cn.iman.snappfood.interview_task.application.arch.controller.EntityViewController
import com.cn.iman.snappfood.interview_task.domain.account.data.AccountEntity
import com.cn.iman.snappfood.interview_task.domain.account.service.AccountService
import io.swagger.v3.oas.annotations.Operation
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/accounts")
class AccountController(override var service: AccountService) : EntityViewController<AccountService, AccountEntity>() {
    @Operation(
        summary = "Create a new entity",
        description = "Creates a new entity. "
    )
    @PostMapping("/create")
    fun create(@RequestBody dto: AccountEntity): ResponseEntity<AccountEntity> {
        return ResponseEntity.ok(service.create(dto))
    }
}