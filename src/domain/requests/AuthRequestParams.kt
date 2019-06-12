package io.photos.domain.requests

import data.exceptions.InvalidParamsException
import data.exceptions.UnsupportedRequestParamsException
import data.exceptions.ValidationException
import io.photos.domain.entities.ParamsValidator
import io.photos.domain.utils.Either
import io.photos.domain.utils.ResultOk

sealed class AuthRequestParams(val login: String, val password: String) : RequestParams {
    class LoginRequestParams(login: String, password: String) : AuthRequestParams(login, password)
    class RegisterRequestParams(val id: Long, login: String, password: String) : AuthRequestParams(login, password)
}

class AuthRequestParamsValidator : ParamsValidator<AuthRequestParams> {
    override fun validate(params: AuthRequestParams): Either<ResultOk, ValidationException> {
        when (params) {
            is AuthRequestParams.LoginRequestParams ->
                return if (params.login.length < 20 && params.password.length < 100)
                    Either.Success(ResultOk)
                else Either.Failure(InvalidParamsException(params, "Username or password are too large"))
            is AuthRequestParams.RegisterRequestParams ->
                return if (params.login.length < 20
                    && params.password.length < 100
                    && params.login.length > 3
                    && params.password.length > 3
                    && params.id > 0L
                )
                    Either.Success(ResultOk)
                else Either.Failure(InvalidParamsException(params))
            else -> throw UnsupportedRequestParamsException(params)
        }
    }
}