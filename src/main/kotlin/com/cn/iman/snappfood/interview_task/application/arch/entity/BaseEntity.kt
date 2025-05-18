package com.cn.iman.snappfood.interview_task.application.arch.entity

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.MappedSuperclass
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.annotation.Version
import java.util.*

@MappedSuperclass
abstract class BaseEntity(
    @Id @GeneratedValue
    var id: Long? = null
) {
    @field:Version
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    var version: Long = 0

    @field:CreatedDate
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    var createdAt: Date? = null

    @field:LastModifiedDate
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    var updatedAt: Date? = null

    fun id(): Long = id ?: throw IllegalStateException("ID is null")
    fun id(id: Long?) {
        this.id = id
    }
}
