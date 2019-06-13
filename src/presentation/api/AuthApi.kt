package io.photos.presentation.api

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.auth.authenticate
import io.ktor.auth.jwt.JWTPrincipal
import io.ktor.auth.jwt.jwt
import io.ktor.auth.principal
import io.ktor.request.receiveParameters
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.post
import io.photos.domain.AuthConfig
import io.photos.domain.usecases.users.auth.AuthUseCase
import io.photos.domain.utils.getApiResponseCode
import io.photos.domain.utils.toApiResponse
import org.koin.core.KoinComponent
import org.koin.core.inject

object AuthApi : KoinComponent {

    private val authConfig by inject<AuthConfig>()
    private val authUseCase by inject<AuthUseCase>()

    fun Application.initAuth() {
        install(Authentication) {
            jwt {
                realm = authConfig.realm
                verifier(makeJwtVerifier(authConfig.issuer, authConfig.secret))
                validate { credential ->
                    JWTPrincipal(credential.payload)
                }
            }
        }

    }

    private fun makeJwtVerifier(jwtIssuer: String, secret: String) = JWT
        .require(Algorithm.HMAC512(secret))
        .withIssuer(jwtIssuer)
        .build()

    fun Routing.auth() {
        post("/login") {
            val params = call.receiveParameters()
            val result = authUseCase.performAuth(params["login"], params["password"])
            call.respond(result.getApiResponseCode(), result.toApiResponse())
        }

        post("/register") {
            val params = call.receiveParameters()
            val result = authUseCase.register(params["login"], params["password"])
            call.respond(result.getApiResponseCode(), result.toApiResponse())
        }

        authenticate {
            post("/refresh_token") {
                val userId = call.principal<JWTPrincipal>()?.payload?.claims?.get("id")
                val result = authUseCase.refreshToken(userId?.asLong())
                call.respond(result.getApiResponseCode(), result.toApiResponse())
            }
        }
    }
}