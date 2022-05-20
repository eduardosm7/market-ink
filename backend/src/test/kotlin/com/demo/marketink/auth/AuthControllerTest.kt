package com.demo.marketink.auth

import com.demo.marketink.AbstractControllerTest
import com.demo.marketink.user.CreateOrUpdateUserDto
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.post

internal class AuthControllerTest : AbstractControllerTest() {

    @Test
    fun `should create user and return created status when register is successful`() {
        // Given
        val credentials = AuthCredentialsDto("name", "password")

        // When
        val apiCall = mockMvc.post("/auth/register") {
            contentType = MediaType.APPLICATION_JSON
            content = mapper.writeValueAsString(credentials)
            accept = MediaType.APPLICATION_JSON
        }

        // Then
        apiCall.andExpect {
            status { isCreated() }
            jsonPath("$.token") { isNotEmpty() }
            jsonPath("$.error") { isEmpty() }
        }

        val user = userService.findByName("name")
        assertThat(user).isNotEmpty
    }

    @Test
    fun `should return unprocessable entity status when register with user name already in use`() {
        // Given
        val credentials = AuthCredentialsDto("name", "password")
        userService.save(CreateOrUpdateUserDto(credentials.name, credentials.password))

        // When
        val apiCall = mockMvc.post("/auth/register") {
            contentType = MediaType.APPLICATION_JSON
            content = mapper.writeValueAsString(credentials)
            accept = MediaType.APPLICATION_JSON
        }

        // Then
        apiCall.andExpect {
            status { isUnprocessableEntity() }
            jsonPath("$.token") { isEmpty() }
            jsonPath("$.error") { value("Username already in use") }
        }
    }

    @Test
    fun `should return token and ok status when login is successful`() {
        // Given
        val credentials = AuthCredentialsDto("name", "password")
        userService.save(CreateOrUpdateUserDto(credentials.name, passwordEncoder.encode(credentials.password)))

        // When
        val apiCall = mockMvc.post("/auth/login") {
            contentType = MediaType.APPLICATION_JSON
            content = mapper.writeValueAsString(credentials)
            accept = MediaType.APPLICATION_JSON
        }

        // Then
        apiCall.andExpect {
            status { isOk() }
            jsonPath("$.token") { isNotEmpty() }
            jsonPath("$.error") { isEmpty() }
        }
    }

    @Test
    fun `should return unauthorized status when login with invalid credentials`() {
        // Given
        val credentials = AuthCredentialsDto("name", "invalidPassword")
        userService.save(CreateOrUpdateUserDto(credentials.name, "otherPassword"))

        // When
        val apiCall = mockMvc.post("/auth/login") {
            contentType = MediaType.APPLICATION_JSON
            content = mapper.writeValueAsString(credentials)
            accept = MediaType.APPLICATION_JSON
        }

        // Then
        apiCall.andExpect {
            status { isUnauthorized() }
            jsonPath("$.token") { isEmpty() }
            jsonPath("$.error") { value("Invalid credentials") }
        }
    }
}
