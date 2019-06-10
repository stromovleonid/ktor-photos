package repositories

import Dependencies.longIdProvider
import Dependencies.userMetadataRepository
import Dependencies.userMetadataRequestsValidator
import invalidUsername
import io.photos.domain.entities.UsernameEntity
import io.photos.domain.requests.UserMetadataRequestParams
import io.photos.domain.utils.Either
import validUsername
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class UserMetadataRepositoryTest {
    @Test
    fun testIdProvider() {
        var initial = longIdProvider.current()
        assertEquals(longIdProvider.provideNext(), initial + 1)
        assertEquals(longIdProvider.provideNext(), initial + 2)

        initial = longIdProvider.current()

        for (index in 0..1000)
            Thread {
                assertEquals(longIdProvider.provideNext(), initial + index + 1)
            }.run()
    }

    @Test
    fun testValidator() {
        assertTrue {
            userMetadataRequestsValidator.validate(
                UserMetadataRequestParams.CreateUserMetadataRequestParams(
                    UsernameEntity(validUsername)
                )
            ) is Either.Success
        }

        assertTrue {
            userMetadataRequestsValidator.validate(
                UserMetadataRequestParams.CreateUserMetadataRequestParams(
                    UsernameEntity(invalidUsername)
                )
            ) is Either.Failure
        }

        assertTrue {
            userMetadataRequestsValidator.validate(
                UserMetadataRequestParams.FindUserMetadataByIdRequestParams(
                    20L
                )
            ) is Either.Success
        }

        assertTrue {
            userMetadataRequestsValidator.validate(
                UserMetadataRequestParams.FindUserMetadataByIdRequestParams(
                    -1L
                )
            ) is Either.Failure
        }

        assertTrue {
            userMetadataRequestsValidator.validate(
                UserMetadataRequestParams.FindUserMetadataByExactUsernameRequestParams(
                    UsernameEntity(validUsername)
                )
            ) is Either.Success
        }

        assertTrue {
            userMetadataRequestsValidator.validate(
                UserMetadataRequestParams.FindUserMetadataByExactUsernameRequestParams(
                    UsernameEntity(invalidUsername)
                )
            ) is Either.Failure
        }
    }

    @Test
    fun testRead() {
        userMetadataRepository.create(
            UserMetadataRequestParams.CreateUserMetadataRequestParams(
                UsernameEntity(
                    validUsername
                )
            )
        )

        val findByNameResult = userMetadataRepository.read(
            UserMetadataRequestParams.FindUserMetadataByExactUsernameRequestParams(
                UsernameEntity(validUsername)
            )
        )
        assertTrue { findByNameResult is Either.Success }
        assertTrue { (findByNameResult as Either.Success).result.username == UsernameEntity(validUsername) }

        val findByIdResult = userMetadataRepository.read(
            UserMetadataRequestParams.FindUserMetadataByIdRequestParams(
                1L
            )
        )
        assertTrue { findByIdResult is Either.Success }
        assertEquals((findByIdResult as Either.Success).result.id, 1L)
    }

    @Test
    fun testCreate() {
        assertTrue {
            userMetadataRepository.create(
                UserMetadataRequestParams.CreateUserMetadataRequestParams(
                    UsernameEntity(
                        validUsername
                    )
                )
            ) is Either.Success
        }

        assertTrue {
            userMetadataRepository.create(
                UserMetadataRequestParams.CreateUserMetadataRequestParams(
                    UsernameEntity(
                        invalidUsername
                    )
                )
            ) is Either.Failure
        }
    }
}