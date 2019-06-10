package api

import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.withTestApplication
import io.photos.module

abstract class ApiTest {
    fun test(block: TestApplicationEngine.() -> Unit) {
        withTestApplication({ module(testing = true) }) {
            block()
        }
    }
}