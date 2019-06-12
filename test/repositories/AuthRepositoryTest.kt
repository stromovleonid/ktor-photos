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
            AuthRequestParams.LoginRequestParams("", "")
        ) is Either.Success}

        assertTrue { authParamsValidator.validate(
            AuthRequestParams.LoginRequestParams("true", "true")
        ) is Either.Success}

        assertTrue { authParamsValidator.validate(
            AuthRequestParams.LoginRequestParams("dasdasdasdajskdfjehrk23jh4j234hk24h23kj4h2", "dasdasdasdajskdfjehrk23jh4j234hk24h23kj4h2dasdasdasdajskdfjehrk23jh4j234hk24h23kj4h2dasdasdasdajskdfjehrk23jh4j234hk24h23kj4h2dasdasdasdajskdfjehrk23jh4j234hk24h23kj4h2dasdasdasdajskdfjehrk23jh4j234hk24h23kj4h2")
        ) is Either.Failure}


        assertTrue { authParamsValidator.validate(
            AuthRequestParams.RegisterRequestParams(1L,"g", "")
        ) is Either.Failure}

        assertTrue { authParamsValidator.validate(
            AuthRequestParams.RegisterRequestParams(1L, "tru", "tru")
        ) is Either.Failure}

        assertTrue { authParamsValidator.validate(
            AuthRequestParams.RegisterRequestParams(1L, "truesdfs", "truesdfsdf")
        ) is Either.Success}

        assertTrue { authParamsValidator.validate(
            AuthRequestParams.RegisterRequestParams(1L, "dasdasdasdajskdfjehrk23jh4j234hk24h23kj4h2", "dasdasdasdajskdfjehrk23jh4j234hk24h23kj4h2dasdasdasdajskdfjehrk23jh4j234hk24h23kj4h2dasdasdasdajskdfjehrk23jh4j234hk24h23kj4h2dasdasdasdajskdfjehrk23jh4j234hk24h23kj4h2dasdasdasdajskdfjehrk23jh4j234hk24h23kj4h2")
        ) is Either.Failure}
    }

    @Test
    fun testRead() {
        val realUser = authRepository.read(AuthRequestParams.LoginRequestParams("test", "test"))

        assertTrue { realUser is Either.Success }
        assertTrue { (realUser as Either.Success).result.id == 1001L }

        val invalidUser = authRepository.read(AuthRequestParams.LoginRequestParams("test1", "test2"))

        assertTrue { invalidUser is Either.Failure }
        assertTrue { (invalidUser as Either.Failure).error is DataNotFoundException}
    }

    @Test
    fun testCreate() {
        val invalidRequest = authRepository.create(AuthRequestParams.RegisterRequestParams(1L, "f", "fsdfsdfdsfsd"))
        assertTrue { invalidRequest.isFailure() }

        val validRequest = authRepository.create(AuthRequestParams.RegisterRequestParams(12L, "fsdfdsfsf", "fsdfsdfdsfsd"))
        assertTrue { validRequest.isSuccess() }
    }
}