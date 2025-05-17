package com.cn.iman.snappfood.interview_task.application.arch.services

import com.cn.iman.snappfood.interview_task.application.arch.entity.BaseEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.repository.NoRepositoryBean

@NoRepositoryBean
interface Repository<T : BaseEntity> : JpaRepository<T, Long>, JpaSpecificationExecutor<T>
