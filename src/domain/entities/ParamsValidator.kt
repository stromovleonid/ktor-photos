package io.photos.domain.entities

import data.exceptions.ValidationException
import io.photos.domain.requests.RequestParams
import io.photos.domain.utils.Either
import io.photos.domain.utils.ResultOk

interface ParamsValidator<P: RequestParams> {
    fun validate(params: P): Either<ResultOk, ValidationException>
}