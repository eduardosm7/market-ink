package com.demo.marketink.user

import java.util.Optional
import java.util.UUID
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Long> {
    fun findByExternalId(externalId: UUID): Optional<User>
}
