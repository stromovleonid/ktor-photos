package usecases

import Dependencies.userMetadataUseCase
import invalidUsername
import io.photos.domain.utils.Either
import kotlinx.coroutines.runBlocking
import validUsername
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class UserMetadataUseCaseTest {
    @Test
    fun testCreate() = runBlocking {
        val useCase = userMetadataUseCase

        val invalidResult = useCase.create(invalidUsername)
        assertTrue { invalidResult is Either.Failure }

        val validResult = useCase.create(validUsername)
        assertTrue { validResult is Either.Success }

        val rand = Random.Default

        for (index in 0..10) {
            val result = useCase.create(validUsername + rand.nextInt(1000))
            assertTrue { result is Either.Success }
        }
    }


    @Test
    fun testFindById() = runBlocking {
        val useCase = userMetadataUseCase
        useCase.create(validUsername)
        val readResult = useCase.findById("1")
        assertTrue { readResult is Either.Success }
        assertEquals((readResult as Either.Success).result.id, 1L)

        val invalidResult = useCase.findById("100")
        assertTrue { invalidResult is Either.Failure }

        val invalidStringResult = useCase.findById("invalid")
        assertTrue { invalidStringResult is Either.Failure }
    }
}