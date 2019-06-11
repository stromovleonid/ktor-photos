package io.photos.data.repositories

import domain.repositories.AbstractRepository
import io.photos.data.providers.IdProvider
import io.photos.domain.entities.ParamsValidator
import io.photos.domain.entities.UserMetadataEntity
import data.exceptions.RepositoryException
import data.exceptions.UnsupportedRequestParamsException
import io.photos.domain.entities.AvatarEntity
import io.photos.domain.entities.UsernameEntity
import io.photos.domain.requests.UserMetadataRequestParams
import io.photos.domain.utils.Either
import io.photos.domain.utils.ResultOk
import java.util.*

class UserMetadataRepository(
    private val idProvider: IdProvider<Long>,
    paramsValidator: ParamsValidator<UserMetadataRequestParams>
) :
    AbstractRepository<UserMetadataEntity, UserMetadataRequestParams>(validator = paramsValidator) {

    private val users = mutableListOf<UserMetadataEntity>().apply {
        add(UserMetadataEntity(1L, UsernameEntity("dsfd"), Date(), AvatarEntity("sdfsdf")))
    }

    override fun performCreate(params: UserMetadataRequestParams): Either<ResultOk, RepositoryException> {
        params as UserMetadataRequestParams.CreateUserMetadataRequestParams
        val newUser = UserMetadataEntity(
            idProvider.provideNext(),
            params.username,
            Date(),
            AvatarEntity()
        )
        users.add(newUser)
        return Either.Success(ResultOk)
    }

    override fun performRead(params: UserMetadataRequestParams): Either<UserMetadataEntity, RepositoryException> {
        return when (params) {
            is UserMetadataRequestParams.FindUserMetadataByIdRequestParams -> {
                users.find { it.id == params.id }
                    .resultOrNotFound(params)
            }

            else -> throw UnsupportedRequestParamsException(params)
        }
    }


    override fun performFindAll(params: UserMetadataRequestParams): Either<List<UserMetadataEntity>, RepositoryException> {
        return when (params) {
            is UserMetadataRequestParams.FindUserMetadataRequestParams -> {
                val fromIndex = params.pageIndex * params.pageSize
                val toIndex = (params.pageIndex + 1) * params.pageSize

                if (fromIndex >= users.size) return Either.Success(emptyList())

                val results = users.filter { it.username.username.contains(params.query, params.ignoreCase) }
                Either.Success(
                    results
                        .subList(fromIndex, if (toIndex <= results.size) toIndex else results.size)
                )
            }

            else -> throw UnsupportedRequestParamsException(params)
        }
    }
}