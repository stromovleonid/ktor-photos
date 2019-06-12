package api

import Dependencies.authUseCase
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.handleRequest
import io.photos.domain.utils.Either
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals

class AuthApiTest: ApiTest() {
    @Test
    fun testLogin() = runBlocking {

        val validToken = authUseCase.performAuth("test", "test")

        test {
            handleRequest(HttpMethod.Post, "/login"){
                addHeader("Authorization", "Bearer invalid_token")
            }.apply {
                assertEquals(HttpStatusCode.Unauthorized, response.status())
            }
        }

        test {
            handleRequest(HttpMethod.Post, "/login").apply {
                assertEquals(HttpStatusCode.Unauthorized, response.status())
            }
        }

        test {
            handleRequest(HttpMethod.Post, "/login"){
                addHeader("Authorization", "Bearer ${(validToken as Either.Success).result.token}")
            }.apply {
                assertEquals(HttpStatusCode.Unauthorized, response.status())
            }
        }
    }
}