package repositories

import Dependencies.authParamsValidator
import Dependencies.authRepository
import data.exceptions.DataNotFoundException
import io.photos.domain.requests.AuthRequestParams
import io.photos.domain.utils.Either
import org.junit.Test
import kotlin.test.assertTrue

class AuthRepositoryTest {
    @Test
    fun testValidator() {
        assertTrue { authParamsValidator.validate(
            AuthRequestParams("", "")
        ) is Either.Success}

        assertTrue { authParamsValidator.validate(
            AuthRequestParams("true", "true")
        ) is Either.Success}

        assertTrue { authParamsValidator.validate(
            AuthRequestParams("dasdasdasdajskdfjehrk23jh4j234hk24h23kj4h2", "dasdasdasdajskdfjehrk23jh4j234hk24h23kj4h2dasdasdasdajskdfjehrk23jh4j234hk24h23kj4h2dasdasdasdajskdfjehrk23jh4j234hk24h23kj4h2dasdasdasdajskdfjehrk23jh4j234hk24h23kj4h2dasdasdasdajskdfjehrk23jh4j234hk24h23kj4h2")
        ) is Either.Failure}
    }

    @Test
    fun testRead() {
        val realUser = authRepository.read(AuthRequestParams("test", "test"))

        assertTrue { realUser is Either.Success }
        assertTrue { (realUser as Either.Success).result.id == 1001L }

        val invalidUser = authRepository.read(AuthRequestParams("test1", "test2"))

        assertTrue { invalidUser is Either.Failure }
        assertTrue { (invalidUser as Either.Failure).error is DataNotFoundException}
    }
}