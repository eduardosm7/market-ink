package com.demo.marketink.config

import com.auth0.jwt.exceptions.JWTVerificationException
import com.demo.marketink.common.JwtService
import com.demo.marketink.user.UserService
import java.io.IOException
import java.lang.Exception
import java.util.Collections
import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val jwtFilter: JwtFilter,
    private val userDetailsService: AuthUserDetailsService
) : WebSecurityConfigurerAdapter() {

    @Throws(Exception::class)
    override fun configure(http: HttpSecurity) {
        http.csrf().disable()
            .httpBasic().disable()
            .cors()
            .and()
            .authorizeHttpRequests()
            .antMatchers("/auth/**").permitAll()
            .antMatchers("/h2-console/**").permitAll()
            .antMatchers("/users/**").hasRole("USER")
            .and()
            .userDetailsService(userDetailsService)
            .exceptionHandling()
            .authenticationEntryPoint { _: HttpServletRequest?, response: HttpServletResponse, _: AuthenticationException? ->
                response.sendError(
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "Unauthorized"
                )
            }
            .and()
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter::class.java)
        http.headers().frameOptions().disable()
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    @Throws(Exception::class)
    override fun authenticationManagerBean(): AuthenticationManager = super.authenticationManagerBean()
}

@Component
class JwtFilter(
    private val userDetailsService: AuthUserDetailsService,
    private val jwtService: JwtService
) : OncePerRequestFilter() {

    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authHeader = request.getHeader(AUTH_HEADER_KEY)

        if (authHeader != null && authHeader.isNotBlank() && authHeader.startsWith(AUTH_HEADER_PREFIX)) {
            val jwt = authHeader.removePrefix(AUTH_HEADER_PREFIX)
            if (jwt.isBlank()) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid JWT Token in Bearer Header")
            } else {
                try {
                    val username: String? = jwtService.validateTokenAndRetrieveSubject(jwt)
                    val userDetails: UserDetails? = username?.let { userDetailsService.loadUserByUsername(it) }
                    val authToken =
                        UsernamePasswordAuthenticationToken(username, userDetails?.password, userDetails?.authorities)
                    if (SecurityContextHolder.getContext().authentication == null) {
                        SecurityContextHolder.getContext().authentication = authToken
                    }
                } catch (e: JWTVerificationException) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid JWT Token")
                }
            }
        }
        filterChain.doFilter(request, response)
    }

    companion object {
        private const val AUTH_HEADER_KEY = "Authorization"
        private const val AUTH_HEADER_PREFIX = "Bearer "
    }
}

@Component
class AuthUserDetailsService(
    private val userService: UserService
) : UserDetailsService {

    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(username: String): UserDetails? {
        val user = userService.findByName(username)
            .orElseThrow { UsernameNotFoundException("Could not find User with name = $username") }

        return User(
            username,
            user.password,
            Collections.singletonList(SimpleGrantedAuthority("ROLE_USER"))
        )
    }
}
