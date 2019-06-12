package io.photos.domain.requests

import data.exceptions.InvalidUsernameException
import data.exceptions.ValidationException
import io.photos.domain.entities.UsernameEntity
import io.photos.domain.utils.Either
import io.photos.domain.utils.ResultOk

const val MAX_USERNAME_LENGTH = 20

fun validateUsername(username: UsernameEntity): Either<ResultOk, ValidationException> {
    return username.let {
        if (it.username.length in (0..MAX_USERNAME_LENGTH))
            Either.Success(ResultOk)
        else Either.Failure(InvalidUsernameException(it.username))
    }
}