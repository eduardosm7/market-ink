package com.demo.marketink.user

import java.util.UUID

data class UserResponseDto(
    val externalId: UUID,
    val name: String
)

data class CreateOrUpdateUserDto(
    val name: String,
    val password: String,
)
