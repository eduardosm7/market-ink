package com.demo.marketink.user

import com.demo.marketink.user.dto.CreateOrUpdateUserDto
import java.util.Optional
import java.util.UUID
import javax.transaction.Transactional
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository,
) {
    fun save(dto: CreateOrUpdateUserDto) {
        userRepository.save(
            User(
                externalId = UUID.randomUUID(),
                name = dto.name,
                password = dto.password
            )
        )
    }

    fun list(): List<User> = userRepository.findAll()

    fun findByExternalId(externalId: UUID): Optional<User> = userRepository.findByExternalId(externalId)

    @Transactional
    fun update(externalId: UUID, dto: CreateOrUpdateUserDto) {
        findByExternalId(externalId).ifPresent {
            it.name = dto.name
            it.password = dto.password
        }
    }

    @Transactional
    fun delete(externalId: UUID) {
        val user = findByExternalId(externalId)
        user.ifPresent {
            it.delete()
        }
    }
}
