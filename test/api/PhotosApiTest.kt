package api

import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.handleRequest
import kotlin.test.Test
import kotlin.test.assertEquals

class PhotosApiTest: ApiTest() {
    @Test
    fun testFeed() {
        test {
            handleRequest(HttpMethod.Get, "/photos").apply {
                assertEquals(HttpStatusCode.OK, response.status())
            }
        }

        test {
            handleRequest(HttpMethod.Get, "/photos?pageSize=10&pageIndex=10").apply {
                assertEquals(HttpStatusCode.OK, response.status())
            }
        }

        test {
            handleRequest(HttpMethod.Get, "/photos?pageSize=a").apply {
                assertEquals(HttpStatusCode.BadRequest, response.status())
            }
        }

        test {
            handleRequest(HttpMethod.Get, "/users?query=123&pageIndex=a").apply {
                assertEquals(HttpStatusCode.BadRequest, response.status())
            }
        }

    }

    @Test
    fun testUser() {
        test {
            handleRequest(HttpMethod.Get, "/photos/1001").apply {
                assertEquals(HttpStatusCode.OK, response.status())
            }
        }

        test {
            handleRequest(HttpMethod.Get, "/photos/1001?pageSize=10&pageIndex=10").apply {
                assertEquals(HttpStatusCode.OK, response.status())
            }
        }

        test {
            handleRequest(HttpMethod.Get, "/photos/1001?pageSize=a").apply {
                assertEquals(HttpStatusCode.BadRequest, response.status())
            }
        }

        test {
            handleRequest(HttpMethod.Get, "/photos/1001?pageIndex=a").apply {
                assertEquals(HttpStatusCode.BadRequest, response.status())
            }
        }

        test {
            handleRequest(HttpMethod.Get, "/photos/1001000?pageSize=10&pageIndex=10").apply {
                assertEquals(HttpStatusCode.OK, response.status())
            }
        }

        test {
            handleRequest(HttpMethod.Get, "/photos/-1001?pageSize=10&pageIndex=10").apply {
                assertEquals(HttpStatusCode.BadRequest, response.status())
            }
        }
    }
}