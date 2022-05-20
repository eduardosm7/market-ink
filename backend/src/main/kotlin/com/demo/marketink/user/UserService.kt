package com.demo.marketink.user

import java.util.UUID
import javax.transaction.Transactional
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository,
) {
    fun save(dto: CreateOrUpdateUserDto) {
        if (findByName(dto.name).isPresent)
            throw UserNameAlreadyInUseException()

        userRepository.save(
            User(
                externalId = UUID.randomUUID(),
                name = dto.name,
                password = dto.password
            )
        )
    }

    fun list(): List<User> = userRepository.findAll()

    fun findByExternalId(externalId: UUID) = userRepository.findByExternalId(externalId)

    fun findByName(name: String) = userRepository.findByName(name);

    @Transactional
    fun update(externalId: UUID, dto: CreateOrUpdateUserDto) {
        if (findByExternalId(externalId).isEmpty)
            throw UserNotFoundException()
        if (findByName(dto.name).isPresent)
            throw UserNameAlreadyInUseException()

        findByExternalId(externalId).ifPresent {
            it.name = dto.name
            it.password = dto.password
        }
    }

    @Transactional
    fun delete(externalId: UUID) {
        if (findByExternalId(externalId).isEmpty)
            throw UserNotFoundException()

        val user = findByExternalId(externalId)
        user.ifPresent {
            it.delete()
        }
    }
}
