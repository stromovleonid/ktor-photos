package io.photos.domain

class AuthConfig(val secret: String = "secret",
                 val tokenValidTimeMs: Long = 24 * 60 * 60 * 1000L,//24hr
                 val realm: String = "Ktor photos",
                 val issuer: String = "https://ktor-photos.com",
                 val audience: String = "jwt-audience")