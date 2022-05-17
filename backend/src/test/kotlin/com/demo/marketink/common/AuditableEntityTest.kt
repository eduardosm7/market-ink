package com.demo.marketink.common

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test


internal class AuditableEntityTest {

    @Test
    fun delete() {
        // Given
        val entity = AuditableEntityDummyImpl()

        // When
        entity.delete()

        // Then
        Assertions.assertThat(entity.isDeleted).isTrue
    }
}

internal class AuditableEntityDummyImpl : AuditableEntity<String>()
