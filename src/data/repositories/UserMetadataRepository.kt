package io.photos.data.repositories

import domain.repositories.AbstractRepository
import io.photos.data.providers.IdProvider
import io.photos.domain.entities.ParamsValidator
import io.photos.domain.entities.UserMetadataEntity
import io.photos.domain.exceptions.DataNotFoundException
import io.photos.domain.exceptions.RepositoryException
import io.photos.domain.exceptions.UnsupportedRequestParamsException
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
            is UserMetadataRequestParams.FindUserMetadataByUsernameRequestParams -> {
                return users.find { it.username == params.username }
                    .let { metadata ->
                        if (metadata != null)
                            Either.Success(metadata)
                        else Either.Failure(DataNotFoundException())
                    }
            }

            is UserMetadataRequestParams.FindUserMetadataByIdRequestParams -> {
                return users.find { it.id == params.id }
                    .let { metadata ->
                        if (metadata != null)
                            Either.Success(metadata)
                        else Either.Failure(DataNotFoundException())
                    }
            }

            else -> throw UnsupportedRequestParamsException(params)
        }
    }
}