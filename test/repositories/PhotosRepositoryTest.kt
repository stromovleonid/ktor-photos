package repositories

import Dependencies.photoParamsValidator
import Dependencies.photosRepository
import io.photos.domain.entities.UserIdEntity
import io.photos.domain.requests.PhotoRequestParams
import io.photos.domain.utils.Either
import org.junit.Test
import java.io.File
import java.util.*
import kotlin.test.assertTrue

class PhotosRepositoryTest {
    @Test
    fun testValidator() {
        assertTrue { photoParamsValidator.validate(
            PhotoRequestParams.PhotosFeedRequestParams(0, 100)
        ) is Either.Success}

        assertTrue { photoParamsValidator.validate(
            PhotoRequestParams.PhotosFeedRequestParams(0, 0)
        ) is Either.Failure}

        assertTrue { photoParamsValidator.validate(
            PhotoRequestParams.PhotosFeedRequestParams(-10, 100)
        ) is Either.Failure}

        assertTrue { photoParamsValidator.validate(
            PhotoRequestParams.FindPhotosOfUserRequestParams(UserIdEntity(1L), 0, 100)
        ) is Either.Success}

        assertTrue { photoParamsValidator.validate(
            PhotoRequestParams.FindPhotosOfUserRequestParams(UserIdEntity(1L),0, 0)
        ) is Either.Failure}

        assertTrue { photoParamsValidator.validate(
            PhotoRequestParams.FindPhotosOfUserRequestParams(UserIdEntity(1L), -10, 100)
        ) is Either.Failure}

        assertTrue { photoParamsValidator.validate(
            PhotoRequestParams.UploadPhotoRequestParams(UserIdEntity(1L), File("dont exist"), Date())
        ) is Either.Failure}

        assertTrue { photoParamsValidator.validate(
            PhotoRequestParams.UploadPhotoRequestParams(UserIdEntity(1L), File("./uploads/test.png"), Date())
        ) is Either.Success}
    }

    @Test
    fun testFindAll() {
        val validFeed = photosRepository.findAll(PhotoRequestParams.PhotosFeedRequestParams(0, 10))
        assertTrue { validFeed.isSuccess() }
        assertTrue { (validFeed as Either.Success).result.size == 10 }

        val invalidFeed = photosRepository.findAll(PhotoRequestParams.PhotosFeedRequestParams(20, 0))
        assertTrue { invalidFeed.isFailure() }

        val emptyFeed = photosRepository.findAll(PhotoRequestParams.PhotosFeedRequestParams(2000, 100))
        assertTrue { emptyFeed.isSuccess() }
        assertTrue { (emptyFeed as Either.Success).result.isEmpty() }
    }

    @Test
    fun testCreate() {
        photosRepository.create(PhotoRequestParams.UploadPhotoRequestParams(
            UserIdEntity(1001L), File("dont exist"), Date()
        )).run {
            assertTrue { this.isFailure() }
        }

        photosRepository.create(PhotoRequestParams.UploadPhotoRequestParams(
            UserIdEntity(1001L), File("./uploads/test.png"), Date()
        )).run {
            assertTrue { this.isSuccess() }
        }
    }
}