package io.photos.domain.requests

import data.exceptions.*
import io.photos.domain.entities.ParamsValidator
import io.photos.domain.entities.UsernameEntity
import io.photos.domain.utils.Either
import io.photos.domain.utils.ResultOk

sealed class UserMetadataRequestParams : RequestParams {
    data class CreateUserMetadataRequestParams(val username: UsernameEntity) : UserMetadataRequestParams()
    data class FindUserMetadataByIdRequestParams(val id: Long) : UserMetadataRequestParams()
    data class FindUserMetadataRequestParams(
        val query: String,
        val ignoreCase: Boolean,
        val pageIndex: Int,
        val pageSize: Int
    ) : UserMetadataRequestParams()
}


class UserMetadataRequestParamsValidator : ParamsValidator<UserMetadataRequestParams> {

    override fun validate(params: UserMetadataRequestParams): Either<ResultOk, ValidationException> {
        when (params) {
            is UserMetadataRequestParams.CreateUserMetadataRequestParams -> {
                return validateUsername(params.username)
            }

            is UserMetadataRequestParams.FindUserMetadataByIdRequestParams -> {
                return if (params.id >= 0)
                    Either.Success(ResultOk)
                else Either.Failure(InvalidUserIdException(params.id))
            }

            is UserMetadataRequestParams.FindUserMetadataRequestParams -> {
                return if (params.pageIndex >= 0
                    && params.pageSize < 101
                    && params.pageSize > 0
                )
                    Either.Success(ResultOk)
                else Either.Failure(InvalidParamsException(params))
            }

            else -> throw UnsupportedRequestParamsException(params)
        }
    }

}