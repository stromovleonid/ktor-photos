package io.photos.domain.utils

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import java.util.*

interface AuthTokenProducer {
    fun produce(userId: Long): String
}

class AuthTokenProducerImpl(private val secret: String, private val issuer: String, private val validMs: Long): AuthTokenProducer {

    override fun produce(userId: Long): String = JWT.create()
        .withSubject("Authentication")
        .withIssuer(issuer)
        .withClaim("id", userId)
        .withExpiresAt(getExpiration(Date()))
        .sign(Algorithm.HMAC512(secret))

    private fun getExpiration(createdAt: Date) = Date(createdAt.time + validMs)

}