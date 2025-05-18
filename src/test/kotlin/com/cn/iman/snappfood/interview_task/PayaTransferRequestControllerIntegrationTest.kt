package com.cn.iman.snappfood.interview_task

import com.cn.iman.snappfood.interview_task.domain.account.data.AccountEntity
import com.cn.iman.snappfood.interview_task.domain.account.data.AccountRepository
import com.cn.iman.snappfood.interview_task.domain.transfer.actor.http.payload.ShebaRequestDto
import com.cn.iman.snappfood.interview_task.domain.transfer.data.TransferRequestRepository
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status


@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PayaTransferRequestControllerIntegrationTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var accountRepository: AccountRepository

    @Autowired
    private lateinit var transferRequestRepository: TransferRequestRepository

    private val validFromIban = "IR000000000000000000000000"
    private val validToIban   = "IR111111111111111111111111"

    @BeforeEach
    fun setup() {
        // clean up repositories
        transferRequestRepository.deleteAll()
        accountRepository.deleteAll()

        // insert test accounts
        val fromAccount = AccountEntity(
            sheba    = validFromIban,
            balance  = 1_000_000L,
            reserved = 0L
        )
        val toAccount = AccountEntity(
            sheba    = validToIban,
            balance  = 500_000L,
            reserved = 0L
        )
        accountRepository.save(fromAccount)
        accountRepository.save(toAccount)
    }

    @Test
    fun `sendShebaRequest - success`() {
        val dto = ShebaRequestDto(
            price = 200_000L,
            fromShebaNumber = validFromIban,
            toShebaNumber = validToIban,
            note = "Test transfer"
        )

        mockMvc.perform(
            post("/api/sheba")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.message").value("Request is saved successfully and is in pending status"))
            .andExpect(jsonPath("$.request.id").exists())
            .andExpect(jsonPath("$.request.price").value(200000))
            .andExpect(jsonPath("$.request.status").value("PENDING"))

        // verify account balances
        val fromAcc = accountRepository.findBySheba(validFromIban)!!
        assert(fromAcc.balance == 800_000L)
        assert(fromAcc.reserved == 200_000L)
    }

    @Test
    fun `sendShebaRequest - invalid IBAN format`() {
        val dto = ShebaRequestDto(
            price = 100_000L,
            fromShebaNumber = "INVALID_IBAN",
            toShebaNumber = validToIban,
            note = null
        )

        mockMvc.perform(
            post("/api/sheba")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `sendShebaRequest - insufficient balance`() {
        val dto = ShebaRequestDto(
            price = 2_000_000L,
            fromShebaNumber = validFromIban,
            toShebaNumber = validToIban,
            note = "Exceeds balance"
        )

        mockMvc.perform(
            post("/api/sheba")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
        )
            .andExpect(status().isUnprocessableEntity)
            .andExpect(jsonPath("$.key").value("insufficient.balance"))
    }

    @Test
    fun `sendShebaRequest - non-existing source account`() {
        val dto = ShebaRequestDto(
            price = 50_000L,
            fromShebaNumber = "IR999999999999999999999999",
            toShebaNumber = validToIban,
            note = "Unknown source"
        )

        mockMvc.perform(
            post("/api/sheba")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
        )
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.message").value("Entity with sheba number ${dto.fromShebaNumber} not found"))
    }

    @Test
    fun `sendShebaRequest - non-existing destination account`() {
        val dto = ShebaRequestDto(
            price = 50_000L,
            fromShebaNumber = validFromIban,
            toShebaNumber = "IR888888888888888888888888",
            note = "Unknown dest"
        )

        mockMvc.perform(
            post("/api/sheba")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
        )
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.message").value("Entity with sheba number ${dto.toShebaNumber} not found"))
    }
}
