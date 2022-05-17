package com.demo.marketink.user.dto

import java.util.UUID

data class UserResponseDto(
    val externalId: UUID,
    val name: String
)
