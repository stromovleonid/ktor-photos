package api

import io.ktor.http.*
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.setBody
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class AuthApiTest: ApiTest() {
    @Test
    fun testLogin() = runBlocking {

        test {
            handleRequest(HttpMethod.Post, "/login"){
                setBody(listOf("login" to "test", "password" to "test").formUrlEncode())
                addHeader(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
            }.apply {
                assertEquals(HttpStatusCode.OK, response.status())
            }
        }

        test {
            handleRequest(HttpMethod.Post, "/login"){
                setBody(listOf("login" to "test2", "password" to "test3").formUrlEncode())
                addHeader(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
            }.apply {
                assertNotEquals(HttpStatusCode.OK, response.status())
            }
        }

        test {
            handleRequest(HttpMethod.Post, "/login"){
                setBody(listOf("login" to "").formUrlEncode())
                addHeader(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
            }.apply {
                assertNotEquals(HttpStatusCode.OK, response.status())
            }
        }
    }

    @Test
    fun testRegister() = runBlocking {
        test {
            handleRequest(HttpMethod.Post, "/register"){
                setBody(listOf("login" to "test", "password" to "test").formUrlEncode())
                addHeader(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
            }.apply {
                assertEquals(HttpStatusCode.BadRequest, response.status())
            }
        }

        test {
            handleRequest(HttpMethod.Post, "/register"){
                setBody(listOf("login" to "test32423", "password" to "test234324").formUrlEncode())
                addHeader(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
            }.apply {
                assertEquals(HttpStatusCode.OK, response.status())
            }
        }

        test {
            handleRequest(HttpMethod.Post, "/register"){
                setBody(listOf("login" to "test", "password" to "test").formUrlEncode())
                addHeader(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
            }.apply {
                assertEquals(HttpStatusCode.BadRequest, response.status())
            }
        }
    }
}