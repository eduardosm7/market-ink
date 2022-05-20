package com.demo.marketink.auth

data class AuthTokenResponseDto(
    val token: String? = null,
    val error: String? = null
)

data class AuthCredentialsDto(
    val name: String,
    val password: String,
)
