package com.demo.marketink.user

import com.demo.marketink.AbstractIntegratedTest
import java.util.UUID
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class UserServiceTest : AbstractIntegratedTest() {

    @Test
    fun `should save user successfully`() {
        // Given
        assertThat(userRepository.findAll()).isEmpty()
        val dto = CreateOrUpdateUserDto("name", "password")

        // When
        userService.save(dto)

        // Then
        val savedUser = userRepository.findByName(dto.name)
        assertThat(savedUser).isNotEmpty
        assertThat(savedUser.get().name).isEqualTo(dto.name)
        assertThat(savedUser.get().password).isEqualTo(dto.password)
    }

    @Test
    fun `should throw exception when save user with user name already in use`() {
        // Given
        val dto = CreateOrUpdateUserDto("name", "password")
        userRepository.save(User(UUID.randomUUID(), dto.name, dto.password))

        // When / Then
        assertThrows<UserNameAlreadyInUseException> {
            userService.save(dto)
        }
    }

    @Test
    fun `should list saved users`() {
        // Given
        val user1 = User(UUID.randomUUID(), "name1", "password")
        val user2 = User(UUID.randomUUID(), "name2", "password")
        val user3 = User(UUID.randomUUID(), "name3", "password")

        // When
        userRepository.save(user1)
        userRepository.save(user2)
        userRepository.save(user3)

        // Then
        val userList = userService.list()
        assertThat(userList).hasSize(3)
        assertThat(userList.stream().map { it.name }).contains(user1.name, user2.name, user3.name)
    }

    @Test
    fun `should find user by name`() {
        // Given
        val user = userRepository.save(User(UUID.randomUUID(), "name", "password"))

        // When
        val retrievedUser = userService.findByName(user.name)

        // Then
        assertThat(retrievedUser).isNotEmpty
        assertThat(retrievedUser.get().name).isEqualTo(user.name)
        assertThat(retrievedUser.get().password).isEqualTo(user.password)
    }

    @Test
    fun `should update user successfully`() {
        // Given
        val user = userRepository.save(User(UUID.randomUUID(), "name", "password"))
        val dto = CreateOrUpdateUserDto("otherName", "otherPassword")

        // When
        userService.update(user.externalId, dto)

        // Then
        val savedUser = userRepository.findById(user.id)
        assertThat(savedUser).isNotEmpty
        assertThat(savedUser.get().name).isEqualTo(dto.name)
        assertThat(savedUser.get().password).isEqualTo(dto.password)
    }

    @Test
    fun `should throw exception when update user that does not exist`() {
        // Given
        userRepository.save(User(UUID.randomUUID(), "name", "password"))
        val dto = CreateOrUpdateUserDto("otherName", "otherPassword")
        val otherExternalId = UUID.randomUUID()

        // When / Then
        assertThrows<UserNotFoundException> { userService.update(otherExternalId, dto) }
    }

    @Test
    fun `should throw exception when update user with name that already exists`() {
        // Given
        val user = userRepository.save(User(UUID.randomUUID(), "name", "password"))
        val dto = CreateOrUpdateUserDto(user.name, "otherPassword")

        // When / Then
        assertThrows<UserNameAlreadyInUseException> { userService.update(user.externalId, dto) }
    }

    @Test
    fun `should delete user successfully`() {
        // Given
        val user = userRepository.save(User(UUID.randomUUID(), "name", "password"))

        // When
        userService.delete(user.externalId)

        // Then
        val savedUser = userRepository.findById(user.id)
        assertThat(savedUser).isNotEmpty
        assertThat(savedUser.get().isDeleted).isTrue
}

    @Test
    fun `should throw exception when delete user that does not exist`() {
        // Given
        userRepository.save(User(UUID.randomUUID(), "name", "password"))
        val otherExternalId = UUID.randomUUID()

        // When / Then
        assertThrows<UserNotFoundException> { userService.delete(otherExternalId) }
    }
}
