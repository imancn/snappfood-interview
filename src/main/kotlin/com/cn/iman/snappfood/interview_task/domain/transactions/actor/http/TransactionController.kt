package com.cn.iman.snappfood.interview_task.domain.transactions.actor.http

import com.cn.iman.snappfood.interview_task.application.arch.controller.EntityViewController
import com.cn.iman.snappfood.interview_task.domain.transactions.data.TransactionEntity
import com.cn.iman.snappfood.interview_task.domain.transactions.service.TransactionService
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/transactions")
class TransactionController(override var service: TransactionService) :
    EntityViewController<TransactionService, TransactionEntity>()