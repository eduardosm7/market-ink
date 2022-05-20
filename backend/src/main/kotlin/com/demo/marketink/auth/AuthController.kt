package com.demo.marketink.auth

import com.demo.marketink.common.JwtService
import com.demo.marketink.user.UserService
import com.demo.marketink.user.CreateOrUpdateUserDto
import com.demo.marketink.user.UserNameAlreadyInUseException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.AuthenticationException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
class AuthController(
    private val userService: UserService,
    private val jwtService: JwtService,
    private val authenticationManager: AuthenticationManager,
    private val passwordEncoder: PasswordEncoder
) {

    @PostMapping("/register")
    fun registerUser(@RequestBody dto: AuthCredentialsDto): ResponseEntity<AuthTokenResponseDto> {
        return try {
            userService.save(CreateOrUpdateUserDto(name = dto.name, password = passwordEncoder.encode(dto.password)))
            ResponseEntity.status(HttpStatus.CREATED)
                .body(AuthTokenResponseDto(token = jwtService.generateToken(dto.name)))
        } catch (e: UserNameAlreadyInUseException) {
            ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(AuthTokenResponseDto(error = e.message))
        }
    }

    @PostMapping("/login")
    fun login(@RequestBody dto: AuthCredentialsDto): ResponseEntity<AuthTokenResponseDto> {
        return try {
            val authInputToken = UsernamePasswordAuthenticationToken(dto.name, dto.password)
            authenticationManager.authenticate(authInputToken)
            ResponseEntity.ok(AuthTokenResponseDto(token = jwtService.generateToken(dto.name)))
        } catch (e: AuthenticationException) {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(AuthTokenResponseDto(error = "Invalid credentials"))
        }
    }
}
