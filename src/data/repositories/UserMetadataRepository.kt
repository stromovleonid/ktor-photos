package io.photos.data.repositories

import domain.repositories.AbstractRepository
import io.photos.data.providers.IdProvider
import data.exceptions.RepositoryException
import data.exceptions.UnsupportedRequestParamsException
import io.photos.domain.entities.*
import io.photos.domain.requests.UserMetadataRequestParams
import io.photos.domain.utils.Either
import io.photos.domain.utils.ResultOk
import java.util.*

class UserMetadataRepository(
    private val idProvider: IdProvider<UserIdEntity>,
    paramsValidator: ParamsValidator<UserMetadataRequestParams>
) :
    AbstractRepository<UserMetadataEntity, UserMetadataRequestParams>(validator = paramsValidator) {

    private val users = mutableListOf<UserMetadataEntity>().apply {
        add(UserMetadataEntity(UserIdEntity(1001L), UsernameEntity("test_username"), Date(), AvatarEntity()))
    }

    override fun performCreate(params: UserMetadataRequestParams): Either<UserMetadataEntity, RepositoryException> {
        params as UserMetadataRequestParams.CreateUserMetadataRequestParams
        val newUser = UserMetadataEntity(
            idProvider.provideNext(),
            params.username,
            Date(),
            AvatarEntity()
        )
        users.add(newUser)
        return Either.Success(newUser)
    }

    override fun performRead(params: UserMetadataRequestParams): Either<UserMetadataEntity, RepositoryException> {
        return when (params) {
            is UserMetadataRequestParams.FindUserMetadataByIdRequestParams -> {
                users.find { it.id.id == params.id }
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
                val toIndexTrimmed = if (toIndex <= results.size) toIndex else results.size
                val fromIndexTrimmed = if (fromIndex > toIndexTrimmed) toIndexTrimmed else fromIndex
                Either.Success(
                    results
                        .subList(fromIndexTrimmed, toIndexTrimmed)
                )
            }

            else -> throw UnsupportedRequestParamsException(params)
        }
    }
}