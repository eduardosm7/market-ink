package com.demo.marketink.common

import java.util.Date
import javax.persistence.EntityListeners
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.MappedSuperclass
import javax.persistence.Temporal
import javax.persistence.TemporalType
import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
abstract class AuditableEntity<U> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0
        protected set

    @CreatedBy
    var createdBy: U? = null
        protected set

    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    var createdDate: Date? = null
        protected set

    @LastModifiedBy
    var lastModifiedBy: U? = null
        protected set

    @LastModifiedDate
    @Temporal(TemporalType.TIMESTAMP)
    var lastModifiedDate: Date? = null
        protected set

    var isDeleted: Boolean = false
        protected set

    fun delete() {
        isDeleted = true
    }
}