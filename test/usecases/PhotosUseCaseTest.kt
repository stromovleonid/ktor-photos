package usecases

import Dependencies.photosUseCase
import io.photos.domain.utils.Either
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertTrue

class PhotosUseCaseTest {
    @Test
    fun testFeed() = runBlocking {
        val invalidPageRequest = photosUseCase.feed("f", "10")
        assertTrue { invalidPageRequest.isFailure() }

        val invalidPageSizeRequest = photosUseCase.feed("f", "10f")
        assertTrue { invalidPageSizeRequest.isFailure() }

        val validRequest = photosUseCase.feed("0", "100")
        assertTrue { validRequest.isSuccess() }
    }

    @Test
    fun testUserPhotos() = runBlocking {
        val invalidUserRequest = photosUseCase.photosOfUser("fg", "0", "10")
        assertTrue { invalidUserRequest.isFailure() }

        val invalidPageRequest = photosUseCase.photosOfUser("1", "f", "10")
        assertTrue { invalidPageRequest.isFailure() }

        val invalidPageSizeRequest = photosUseCase.photosOfUser("1", "f", "10f")
        assertTrue { invalidPageSizeRequest.isFailure() }

        val validRequest = photosUseCase.photosOfUser("1001", "0", "100")
        assertTrue { validRequest.isSuccess() }

        val validFeed = photosUseCase.feed("0", "100")
        assertTrue { (validRequest as Either.Success).result != (validFeed as Either.Success).result }

    }
}