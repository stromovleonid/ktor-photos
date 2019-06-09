package io.photos.repositories

import io.photos.domain.entities.Username
import io.photos.domain.requests.UserMetadataRequestParams
import io.photos.domain.utils.Either
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
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
                    Username(validUsername)
                )
            ) is Either.Success
        }

        assertTrue {
            userMetadataRequestsValidator.validate(
                UserMetadataRequestParams.CreateUserMetadataRequestParams(
                    Username(invalidUsername)
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
                UserMetadataRequestParams.FindUserMetadataByUsernameRequestParams(
                    Username(validUsername)
                )
            ) is Either.Success
        }

        assertTrue {
            userMetadataRequestsValidator.validate(
                UserMetadataRequestParams.FindUserMetadataByUsernameRequestParams(
                    Username(invalidUsername)
                )
            ) is Either.Failure
        }
    }

    @Test
    fun testRead() {
        userMetadataRepository.create(
            UserMetadataRequestParams.CreateUserMetadataRequestParams(
                Username(
                    validUsername
                )
            )
        )

        val findByNameResult = userMetadataRepository.read(
            UserMetadataRequestParams.FindUserMetadataByUsernameRequestParams(
                Username(validUsername)
            )
        )
        assertTrue { findByNameResult is Either.Success }
        assertTrue { (findByNameResult as Either.Success).result.username == Username(validUsername) }

        val initial = longIdProvider.current()

        val findByIdResult = userMetadataRepository.read(
            UserMetadataRequestParams.FindUserMetadataByIdRequestParams(
                initial
            )
        )
        assertTrue { findByIdResult is Either.Success }
        assertTrue { (findByIdResult as Either.Success).result.id == initial }
    }

    @Test
    fun testCreate() {
        assertTrue {
            userMetadataRepository.create(
                UserMetadataRequestParams.CreateUserMetadataRequestParams(
                    Username(
                        validUsername
                    )
                )
            ) is Either.Success
        }

        assertTrue {
            userMetadataRepository.create(
                UserMetadataRequestParams.CreateUserMetadataRequestParams(
                    Username(
                        invalidUsername
                    )
                )
            ) is Either.Failure
        }
    }
}