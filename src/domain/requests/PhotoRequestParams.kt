package io.photos.domain.requests

import data.exceptions.InvalidParamsException
import data.exceptions.UnsupportedRequestParamsException
import data.exceptions.ValidationException
import io.photos.domain.entities.ParamsValidator
import io.photos.domain.entities.UserIdEntity
import io.photos.domain.utils.Either
import io.photos.domain.utils.ResultOk

sealed class PhotoRequestParams : RequestParams {

    data class PhotosFeedRequestParams(
        val pageIndex: Int,
        val pageSize: Int
    ) : PhotoRequestParams()

    data class FindPhotosOfUserRequestParams(
        val authorId: UserIdEntity,
        val pageIndex: Int,
        val pageSize: Int
    ) : PhotoRequestParams()

    data class AddPhotoToUserRequestParams(val authorId: UserIdEntity, val photo: String) : PhotoRequestParams()
}

class PhotoRequestParamsValidator : ParamsValidator<PhotoRequestParams> {
    override fun validate(params: PhotoRequestParams): Either<ResultOk, ValidationException> {
        when (params) {
            is PhotoRequestParams.FindPhotosOfUserRequestParams -> {
                return if (params.pageIndex >= 0
                    && params.pageSize < 101
                    && params.pageSize > 0
                )
                    Either.Success(ResultOk)
                else Either.Failure(InvalidParamsException(params))
            }
            is PhotoRequestParams.PhotosFeedRequestParams -> {
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