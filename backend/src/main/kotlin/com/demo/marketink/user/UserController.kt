package com.demo.marketink.user

import com.demo.marketink.user.dto.CreateOrUpdateUserDto
import com.demo.marketink.user.dto.UserResponseDto
import java.util.Optional
import java.util.UUID
import java.util.stream.Collectors
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/users")
class UserController(
    private val userService: UserService
) {
    @PostMapping
    fun createUser(@RequestBody dto: CreateOrUpdateUserDto): ResponseEntity<Void> {
        userService.save(dto)
        return ResponseEntity.status(HttpStatus.CREATED).build()
    }

    @GetMapping
    fun listUsers(): ResponseEntity<List<UserResponseDto>> =
        ResponseEntity.ok(userService.list().stream().map {
            UserResponseDto(
                externalId = it.externalId,
                name = it.name
            )
        }.collect(Collectors.toList()))

    @GetMapping("/{externalId}")
    fun getUser(@PathVariable externalId: UUID): ResponseEntity<Optional<UserResponseDto>> =
        ResponseEntity.ok(userService.findByExternalId(externalId).map {
            UserResponseDto(
                externalId = it.externalId,
                name = it.name
            )
        })

    @PutMapping("/{externalId}")
    fun updateUser(@PathVariable externalId: UUID, @RequestBody dto: CreateOrUpdateUserDto): ResponseEntity<Void> {
        userService.update(externalId, dto)
        return ResponseEntity.noContent().build()
    }

    @DeleteMapping("/{externalId}")
    fun deleteUser(@PathVariable externalId: UUID): ResponseEntity<Void> {
        userService.delete(externalId)
        return ResponseEntity.noContent().build()
    }
}
