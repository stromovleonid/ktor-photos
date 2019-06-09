package io.photos.domain.requests

import io.photos.domain.entities.ParamsValidator
import io.photos.domain.entities.UsernameEntity
import data.exceptions.InvalidUserIdException
import data.exceptions.InvalidUsernameException
import data.exceptions.UnsupportedRequestParamsException
import data.exceptions.ValidationException
import io.photos.domain.utils.Either
import io.photos.domain.utils.ResultOk

sealed class UserMetadataRequestParams : RequestParams {
    data class CreateUserMetadataRequestParams(val username: UsernameEntity) : UserMetadataRequestParams()
    data class FindUserMetadataByIdRequestParams(val id: Long) : UserMetadataRequestParams()
    data class FindUserMetadataByExactUsernameRequestParams(val username: UsernameEntity) : UserMetadataRequestParams()
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

            is UserMetadataRequestParams.FindUserMetadataByExactUsernameRequestParams -> {
                return validateUsername(params.username)
            }

            else -> throw UnsupportedRequestParamsException(params)
        }
    }


    private fun validateUsername(username: UsernameEntity): Either<ResultOk, ValidationException> {
        return username.let {
            if (it.username.length in (0..20))
                Either.Success(ResultOk)
            else Either.Failure(InvalidUsernameException(it.username))
        }
    }

}