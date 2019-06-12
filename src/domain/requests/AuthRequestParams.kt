package io.photos.domain.requests

import data.exceptions.InvalidParamsException
import data.exceptions.ValidationException
import io.photos.domain.entities.ParamsValidator
import io.photos.domain.utils.Either
import io.photos.domain.utils.ResultOk

data class AuthRequestParams(val login: String, val password: String): RequestParams

class AuthRequestParamsValidator: ParamsValidator<AuthRequestParams> {
    override fun validate(params: AuthRequestParams): Either<ResultOk, ValidationException> {
        return if (params.login.length < 20 && params.password.length < 100)
            Either.Success(ResultOk)
        else Either.Failure(InvalidParamsException(params, "Username or password are too large"))
    }
}