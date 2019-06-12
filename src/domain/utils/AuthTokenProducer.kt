package io.photos.domain.utils

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.photos.domain.entities.UserEntity
import java.util.*

interface AuthTokenProducer {
    fun produce(user: UserEntity): String
}

class AuthTokenProducerImpl(private val secret: String, private val issuer: String, private val validMs: Long): AuthTokenProducer {

    override fun produce(user: UserEntity): String = JWT.create()
        .withSubject("Authentication")
        .withIssuer(issuer)
        .withClaim("id", user.id)
        .withExpiresAt(getExpiration(Date()))
        .sign(Algorithm.HMAC512(secret))

    private fun getExpiration(createdAt: Date) = Date(createdAt.time + validMs)

}