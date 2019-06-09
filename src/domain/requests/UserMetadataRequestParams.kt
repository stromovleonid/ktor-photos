package io.photos.domain.requests

import io.photos.domain.entities.ParamsValidator
import io.photos.domain.entities.Username
import io.photos.domain.exceptions.InvalidUserIdException
import io.photos.domain.exceptions.InvalidUsernameException
import io.photos.domain.exceptions.UnsupportedRequestParamsException
import io.photos.domain.exceptions.ValidationException
import io.photos.domain.utils.Either
import io.photos.domain.utils.ResultOk

sealed class UserMetadataRequestParams : RequestParams {
    data class CreateUserMetadataRequestParams(val username: Username) : UserMetadataRequestParams()
    data class FindUserMetadataByIdRequestParams(val id: Long) : UserMetadataRequestParams()
    data class FindUserMetadataByUsernameRequestParams(val username: Username) : UserMetadataRequestParams()
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

            is UserMetadataRequestParams.FindUserMetadataByUsernameRequestParams -> {
                return validateUsername(params.username)
            }

            else -> throw UnsupportedRequestParamsException(params)
        }
    }


    private fun validateUsername(username: Username): Either<ResultOk, ValidationException> {
        return username.let {
            if (it.username.length in (0..20))
                Either.Success(ResultOk)
            else Either.Failure(InvalidUsernameException(it.username))
        }
    }

}