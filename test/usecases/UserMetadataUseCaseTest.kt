package usecases

import dispatchersProvider
import invalidUsername
import io.photos.domain.exceptions.ModelValidationException
import io.photos.domain.model.UsernameModel
import io.photos.domain.utils.Either
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import userMetadataUseCase
import validUsername
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class UserMetadataUseCaseTest {
    @Test
    fun testCreate() {
        val useCase = userMetadataUseCase

        val invalidResult = useCase.createAsync(UsernameModel(invalidUsername))

        CoroutineScope(dispatchersProvider.default).launch {
            val result = invalidResult.await()
            assertTrue { result is Either.Failure }
            assertTrue { (result as Either.Failure).error is ModelValidationException }
        }

        val validResult = useCase.createAsync(UsernameModel(validUsername))

        CoroutineScope(dispatchersProvider.default).launch {
            val result = validResult.await()
            assertTrue { result is Either.Success }
        }

        val rand = Random.Default

        for (index in 0..1000)
            CoroutineScope(dispatchersProvider.default).launch {
                val result = useCase.createAsync(UsernameModel(validUsername + rand.nextInt(1000))).await()
                assertTrue { result is Either.Success }
            }
    }


    @Test
    fun testFindById() {
        CoroutineScope(dispatchersProvider.default).launch {
            val useCase = userMetadataUseCase
            val validResult = useCase.createAsync(UsernameModel(validUsername))
            validResult.await()
            val readResult = useCase.findByIdAsync(1L).await()
            assertTrue { readResult is Either.Success }
            assertEquals((readResult as Either.Success).result.id, 1L)

            val invalidResult = useCase.findByIdAsync(100L).await()
            assertTrue { invalidResult is Either.Failure }
        }
    }
}