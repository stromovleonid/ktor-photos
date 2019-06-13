package usecases

import Dependencies.authUseCase
import io.photos.domain.exceptions.AlreadyTakenException
import io.photos.domain.utils.Either
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

    @Test
    fun testRegister() = runBlocking {
        authUseCase.register(null, null).run {
            assertTrue { this.isFailure() }
        }

        authUseCase.register("", "sdfsdfsdfsdf").run {
            assertTrue { this.isFailure() }
        }

        authUseCase.register("test", "test").run {
            assertTrue { this.isFailure() }
        }


        authUseCase.register("testtesttest", "testtesttest").run {
            assertTrue { this.isSuccess() }
        }

        authUseCase.register("testtesttest", "testtesttest").run {
            assertTrue { this.isFailure() }
            assertTrue { (this as Either.Failure).error is AlreadyTakenException }
        }
    }

    @Test
    fun testRefresh() = runBlocking {
        authUseCase.refreshToken(1001).run {
            assertTrue { this.isSuccess() }
        }

        authUseCase.refreshToken(100100).run {
            assertTrue { this.isFailure() }
        }
    }
}