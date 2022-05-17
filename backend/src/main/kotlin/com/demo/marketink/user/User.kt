package com.demo.marketink.user

import com.demo.marketink.common.AuditableEntity
import java.util.UUID
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Table

@Entity
@Table(name = "users")
class User(
    @Column(nullable = false)
    var externalId: UUID,
    @Column(nullable = false)
    var name: String,
    @Column(nullable = false)
    var password: String
) : AuditableEntity<String>()