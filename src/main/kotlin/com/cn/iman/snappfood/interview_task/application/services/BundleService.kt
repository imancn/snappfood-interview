package com.cn.iman.snappfood.interview_task.application.services

import com.cn.iman.snappfood.interview_task.application.arch.controller.payload.response.MessageResponse
import org.springframework.stereotype.Service
import java.text.MessageFormat
import java.util.*

@Service
class BundleService(
    private val bundle: ResourceBundle
) {
    fun getMessageResponse(key: String = "general.error"): MessageResponse {
        val message = try {
            bundle.getString(key).let { template -> MessageFormat.format(template) }
        } catch (_: Exception) {
            key
        }
        return MessageResponse(key, message)
    }

    fun getMessageResponse(key: String = "general.error", vararg args: String): MessageResponse {
        val message = try {
            bundle.getString(key).let { template -> MessageFormat.format(template, args) }
        } catch (_: Exception) {
            key
        }
        return MessageResponse(key, message)
    }
}