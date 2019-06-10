package api

import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.handleRequest
import kotlinx.coroutines.runBlocking
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class UsersApiTest: ApiTest() {
    @Test
    fun testFindById() = runBlocking {

        test {
            handleRequest(HttpMethod.Get, "/users/1000").apply {
                assertEquals(HttpStatusCode.NotFound, response.status())
                assertTrue { response.content?.contains("Not found") == true }
            }
        }

        test {
            handleRequest(HttpMethod.Get, "/users/1").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertTrue { response.content?.contains("\"username\":\"TestUser\"") == true }
            }
        }

    }


    @Test
    fun testSearchAll() = runBlocking {
        Dependencies.userMetadataUseCase.create("TestUser")


        test {
            handleRequest(HttpMethod.Get, "/users/").apply {
                assertEquals(HttpStatusCode.OK, response.status())
            }
        }

        test {
            handleRequest(HttpMethod.Get, "/users?query=123").apply {
                assertEquals(HttpStatusCode.OK, response.status())
            }
        }

        test {
            handleRequest(HttpMethod.Get, "/users?query=123&pageSize=a").apply {
                assertEquals(HttpStatusCode.BadRequest, response.status())
            }
        }

        test {
            handleRequest(HttpMethod.Get, "/users?query=123&pageIndex=a").apply {
                assertEquals(HttpStatusCode.BadRequest, response.status())
            }
        }

        test {
            handleRequest(HttpMethod.Get, "/users?query=test&ignoreCase=true").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertTrue { response.content?.contains("\"username\":\"TestUser\"") == true }
            }
        }

    }

}
