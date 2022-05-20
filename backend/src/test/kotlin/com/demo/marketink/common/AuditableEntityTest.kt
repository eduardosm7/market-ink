package com.demo.marketink.common

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test


internal class AuditableEntityTest {

    internal class AuditableEntityDummyImpl : AuditableEntity<String>()

    @Test
    fun `should set isDeleted to true when delete is called`() {
        // Given
        val entity = AuditableEntityDummyImpl()

        // When
        entity.delete()

        // Then
        assertThat(entity.isDeleted).isTrue
    }
}
