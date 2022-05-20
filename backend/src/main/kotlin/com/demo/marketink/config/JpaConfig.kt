package com.demo.marketink.config

import java.util.Optional
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.domain.AuditorAware
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
class JpaConfig {
    @Bean
    fun auditorAware(): AuditorAware<String> = AuditorAwareImpl()
}

class AuditorAwareImpl : AuditorAware<String> {
    // Can use Spring Security to return currently logged in user
    // return SecurityContextHolder.getContext().authentication.principal as String
    // TODO: Configure proper current auditor
    override fun getCurrentAuditor(): Optional<String> = Optional.of("dummy")
}
