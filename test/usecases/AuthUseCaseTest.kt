package usecases

import Dependencies.authUseCase
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertTrue

class AuthUseCaseTest {
    @Test
    fun testLogin() = runBlocking {
        authUseCase.performAuth(null, null).run {
            assertTrue { this.isFailure() }
        }

        authUseCase.performAuth(null, "test").run {
            assertTrue { this.isFailure() }
        }

        authUseCase.performAuth("test", null).run {
            assertTrue { this.isFailure() }
        }

        authUseCase.performAuth("test", "test").run {
            assertTrue { this.isSuccess() }
        }

        authUseCase.performAuth("test1", "test1").run {
            assertTrue { this.isFailure() }
        }
    }
}