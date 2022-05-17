package com.demo.marketink.user

import com.demo.marketink.common.AuditableEntity
import java.util.UUID
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Table
import org.hibernate.annotations.Where

@Entity
@Table(name = "users")
@Where(clause = "is_deleted = 'false'")
class User(
    @Column(nullable = false)
    var externalId: UUID,
    @Column(nullable = false)
    var name: String,
    @Column(nullable = false)
    var password: String
) : AuditableEntity<String>()
