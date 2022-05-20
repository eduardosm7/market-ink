package com.demo.marketink.common

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import java.time.Duration
import java.time.Instant
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
class JwtService(
    @Value("\${jwt_secret}")
    private val secret: String
) {

    @Throws(IllegalArgumentException::class, JWTCreationException::class)
    fun generateToken(username: String): String = JWT.create()
        .withSubject(SUBJECT)
        .withClaim(CLAIM, username)
        .withIssuedAt(Date())
        .withIssuer(ISSUER)
        .withExpiresAt(Date.from(Instant.now().plus(Duration.ofHours(1))))
        .sign(Algorithm.HMAC256(secret))

    @Throws(JWTVerificationException::class)
    fun validateTokenAndRetrieveSubject(token: String?): String? {
        val verifier = JWT.require(Algorithm.HMAC256(secret))
            .withSubject(SUBJECT)
            .withIssuer(ISSUER)
            .build()
        val jwt = verifier.verify(token)
        return jwt.getClaim(CLAIM).asString()
    }

    companion object {
        private const val SUBJECT = "User Details"
        private const val ISSUER = "Market Ink"
        private const val CLAIM = "username"
    }
}
