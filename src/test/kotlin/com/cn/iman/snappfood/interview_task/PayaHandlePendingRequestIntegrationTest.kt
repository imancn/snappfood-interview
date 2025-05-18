package com.cn.iman.snappfood.interview_task

import com.cn.iman.snappfood.interview_task.domain.account.data.AccountEntity
import com.cn.iman.snappfood.interview_task.domain.account.data.AccountRepository
import com.cn.iman.snappfood.interview_task.domain.account.service.AccountService
import com.cn.iman.snappfood.interview_task.domain.transactions.data.TransactionRepository
import com.cn.iman.snappfood.interview_task.domain.transactions.service.TransactionService
import com.cn.iman.snappfood.interview_task.domain.transfer.actor.http.payload.HandlePendingRequestDto
import com.cn.iman.snappfood.interview_task.domain.transfer.data.TransferRequestEntity
import com.cn.iman.snappfood.interview_task.domain.transfer.data.TransferRequestRepository
import com.fasterxml.jackson.databind.ObjectMapper
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.test.assertTrue

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class PayaHandlePendingRequestIntegrationTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Autowired
    lateinit var accountRepository: AccountRepository

    @Autowired
    lateinit var accountService: AccountService

    @Autowired
    lateinit var transferRequestRepository: TransferRequestRepository

    @Autowired
    lateinit var transactionRepository: TransactionRepository

    @Autowired
    lateinit var transactionService: TransactionService

    lateinit var fromAccount: AccountEntity
    lateinit var toAccount: AccountEntity
    lateinit var request: TransferRequestEntity

    @BeforeEach
    fun setup() {
        // Clean database
        transactionRepository.deleteAll()
        transferRequestRepository.deleteAll()
        accountRepository.deleteAll()

        // Create source and destination accounts
        fromAccount = accountRepository.save(
            AccountEntity(
                sheba = "IR000000000000000000000001",
                balance = 1000L,
                reserved = 0L
            )
        )
        toAccount = accountRepository.save(
            AccountEntity(
                sheba = "IR000000000000000000000002",
                balance = 500L,
                reserved = 0L
            )
        )

        // Simulate pending transfer reservation: deduct and reserve
        val amount = 200L
        fromAccount.balance -= amount
        fromAccount.reserved += amount
        accountRepository.save(fromAccount)

        // Create pending request
        request = transferRequestRepository.save(
            TransferRequestEntity(
                price = amount,
                fromShebaNumber = fromAccount.sheba,
                toShebaNumber = toAccount.sheba,
                status = TransferRequestEntity.TransferStatus.PENDING
            )
        )
    }

    @Test
    fun `confirm pending request should succeed`() {
        val dto = HandlePendingRequestDto(status = TransferRequestEntity.TransferStatus.CONFIRMED, note = "OK")

        mockMvc.perform(
            put("/api/sheba/${request.id}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.request.status").value(TransferRequestEntity.TransferStatus.CONFIRMED.name))

        // Reload entities
        val updatedFrom = accountRepository.findById(fromAccount.id!!).get()
        val updatedTo = accountRepository.findById(toAccount.id!!).get()

        // Reserved should be released, balance should remain deducted
        assertThat(updatedFrom.reserved).isEqualTo(0L)
        assertThat(updatedFrom.balance).isEqualTo(800L)

        // Destination should be credited
        assertThat(updatedTo.balance).isEqualTo(700L)
    }

    @Test
    fun `cancel pending request should refund`() {
        val dto = HandlePendingRequestDto(status = TransferRequestEntity.TransferStatus.CANCELED, note = "Reject")

        mockMvc.perform(
            put("/api/sheba/${request.id}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.request.status").value(TransferRequestEntity.TransferStatus.CANCELED.name))

        val updatedFrom = accountRepository.findById(fromAccount.id!!).get()

        // Reserved returned
        assertThat(updatedFrom.balance).isEqualTo(1000L)
        assertThat(updatedFrom.reserved).isEqualTo(0L)
    }

    @Test
    fun `handle non-existent request should return 404`() {
        val dto = HandlePendingRequestDto(status = TransferRequestEntity.TransferStatus.CONFIRMED, note = null)

        mockMvc.perform(
            put("/api/sheba/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
        ).andExpect(status().isNotFound)
    }

    @Test
    fun `handle already processed request should return 422`() {
        // First, confirm it
        val id = transferRequestRepository.save(
            request.copy(status = TransferRequestEntity.TransferStatus.CONFIRMED)
        ).id

        val dto = HandlePendingRequestDto(status = TransferRequestEntity.TransferStatus.CONFIRMED, note = null)

        mockMvc.perform(
            put("/api/sheba/$id")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
        )
            .andExpect(status().isUnprocessableEntity)
    }

    @Test
    fun `invalid status in payload should return 400`() {
        val dto = mapOf("status" to "INVALID", "note" to "-")

        mockMvc.perform(
            put("/api/sheba/${request.id}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `concurrent handlePendingRequest calls should only allow one confirmation`() {
        // Setup: pending request of 300 from A to B
        val from = accountRepository.save(AccountEntity("IR00000000000000000000A03", 1000, 0))
        val to   = accountRepository.save(AccountEntity("IR00000000000000000000B04",    0, 0))
        // reserve on creation
        val req = transferRequestRepository.save(
            TransferRequestEntity(
                price = 300,
                fromShebaNumber = from.sheba,
                toShebaNumber   = to.sheba,
                status = TransferRequestEntity.TransferStatus.PENDING,
            )
        )
        accountService.lockBalance(from.id!!, 300)
        transactionService.recordDeduction(from.id(), 300)

        val latch = CountDownLatch(1)
        val results = Collections.synchronizedList(mutableListOf<MvcResult>())

        val pool = Executors.newFixedThreadPool(2)
        repeat(2) {
            pool.submit {
                latch.await()
                val dto = HandlePendingRequestDto(
                    status = TransferRequestEntity.TransferStatus.CONFIRMED,
                    note   = "concurrent"
                )
                val mvc = mockMvc.perform(
                    put("/api/sheba/{id}", req.id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                ).andReturn()
                results += mvc
            }
        }
        latch.countDown()
        pool.shutdown()
        assertTrue(pool.awaitTermination(5, TimeUnit.SECONDS))

        // one 200 OK, one 422 Unprocessable
        val statuses = results.map { it.response.status }
        assertThat(statuses).containsExactlyInAnyOrder(200, 422)

        // A’s final: reserved removed (300), to balance debited and credited
        val updatedFrom = accountRepository.findById(from.id!!).get()
        val updatedTo   = accountRepository.findById(to.id!!).get()
        assertThat(updatedFrom.reserved).isEqualTo(0)
        assertThat(updatedTo.balance).isEqualTo(300)
    }

}
