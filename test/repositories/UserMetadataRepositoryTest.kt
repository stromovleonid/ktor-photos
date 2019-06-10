package repositories

import Dependencies.longIdProvider
import Dependencies.userMetadataRepository
import Dependencies.userMetadataRequestsValidator
import invalidUsername
import io.photos.domain.entities.UsernameEntity
import io.photos.domain.requests.UserMetadataRequestParams
import io.photos.domain.utils.Either
import secondValidUsername
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
                UserMetadataRequestParams.FindUserMetadataRequestParams(
                    validUsername, true, 0, 20
                )
            ) is Either.Success
        }

        assertTrue {
            userMetadataRequestsValidator.validate(
                UserMetadataRequestParams.FindUserMetadataRequestParams(
                    validUsername, true, -20, 10000
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

        val findByIdResult = userMetadataRepository.read(
            UserMetadataRequestParams.FindUserMetadataByIdRequestParams(
                1L
            )
        )
        assertTrue { findByIdResult is Either.Success }
        assertEquals((findByIdResult as Either.Success).result.id, 1L)
    }

    @Test
    fun testFindAll() {
        val usernames = listOf(validUsername + 1, validUsername + 2, validUsername + 3, validUsername + 4, validUsername + 5,
            secondValidUsername + 1, secondValidUsername + 2, secondValidUsername + 3, secondValidUsername + 4,
            secondValidUsername + 5).map { UsernameEntity(it) }

        usernames.forEach {
            userMetadataRepository.create(
                UserMetadataRequestParams.CreateUserMetadataRequestParams(it)
            )
        }

        userMetadataRepository.findAll(UserMetadataRequestParams.FindUserMetadataRequestParams(
            validUsername, true, 0, 50
        )).run {
            assertTrue {this is Either.Success}
            assertEquals((this as Either.Success).result.size, 5)
        }

        userMetadataRepository.findAll(UserMetadataRequestParams.FindUserMetadataRequestParams(
            validUsername, true, 50, 50
        )).run {
            assertTrue {this is Either.Success}
            assertEquals((this as Either.Success).result.size, 0)
        }

        userMetadataRepository.findAll(UserMetadataRequestParams.FindUserMetadataRequestParams(
            validUsername, true, -1, 1000
        )).run {
            assertTrue { this is Either.Failure}
        }

        userMetadataRepository.findAll(UserMetadataRequestParams.FindUserMetadataRequestParams(
            "", true, 1, 2
        )).run {
            assertTrue {this is Either.Success}
            assertEquals((this as Either.Success).result.size, 2)
        }


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