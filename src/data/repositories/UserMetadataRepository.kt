package io.photos.data.repositories

import domain.repositories.AbstractRepository
import io.photos.data.providers.IdProvider
import io.photos.domain.entities.ParamsValidator
import io.photos.domain.entities.UserMetadataEntity
import data.exceptions.DataNotFoundException
import data.exceptions.RepositoryException
import data.exceptions.UnsupportedRequestParamsException
import io.photos.domain.entities.UsernameEntity
import io.photos.domain.requests.UserMetadataRequestParams
import io.photos.domain.utils.Either
import io.photos.domain.utils.ResultOk

class UserMetadataRepository(
    private val idProvider: IdProvider<Long>,
    paramsValidator: ParamsValidator<UserMetadataRequestParams>
) :
    AbstractRepository<UserMetadataEntity, UserMetadataRequestParams>(validator = paramsValidator) {

    private val users = mutableListOf<UserMetadataEntity>()

    override fun performCreate(params: UserMetadataRequestParams): Either<ResultOk, RepositoryException> {
        params as UserMetadataRequestParams.CreateUserMetadataRequestParams
        val newUser = UserMetadataEntity(idProvider.provideNext(), params.username)
        users.add(newUser)
        return Either.Success(ResultOk)
    }

    override fun performRead(params: UserMetadataRequestParams): Either<UserMetadataEntity, RepositoryException> {
        when (params) {
            is UserMetadataRequestParams.FindUserMetadataByExactUsernameRequestParams -> {
                return users.find { it.username == params.username }
                    .let { metadata ->
                        if (metadata != null)
                            Either.Success(metadata)
                        else Either.Failure(DataNotFoundException(this::class, params))
                    }
            }

            is UserMetadataRequestParams.FindUserMetadataByIdRequestParams -> {
                return users.find { it.id == params.id }
                    .let { metadata ->
                        if (metadata != null)
                            Either.Success(metadata)
                        else Either.Failure(DataNotFoundException(this::class, params))
                    }
            }

            else -> throw UnsupportedRequestParamsException(params)
        }
    }
}