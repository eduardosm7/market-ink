package com.demo.marketink

import com.demo.marketink.user.UserRepository
import com.demo.marketink.user.UserService
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.web.servlet.MockMvc

@SpringBootTest
abstract class AbstractIntegratedTest {
    @Autowired protected lateinit var userService: UserService
    @Autowired protected lateinit var userRepository: UserRepository
    @Autowired protected lateinit var mapper: ObjectMapper
    @Autowired protected lateinit var passwordEncoder: PasswordEncoder

    @BeforeEach
    fun cleanDatabase() {
        userRepository.deleteAll()
    }
}

@AutoConfigureMockMvc
abstract class AbstractControllerTest : AbstractIntegratedTest() {
    @Autowired protected lateinit var mockMvc: MockMvc
}
