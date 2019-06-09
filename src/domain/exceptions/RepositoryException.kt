package io.photos.domain.exceptions

import io.photos.domain.requests.RequestParams
import java.lang.Exception
import java.lang.IllegalArgumentException
import kotlin.reflect.KClass

abstract class RepositoryException: Exception()

class InvalidRequestParamsException(repositoryClass: KClass<*>,
                                    params: RequestParams,
                                    validationException: ValidationException): RepositoryException() {
    override val message = "Invalid params $params for class ${repositoryClass.simpleName} (${validationException.message})"

    override val cause = validationException
}

class UnsupportedRequestParamsException(params: RequestParams): RepositoryException() {
    override val message = "Unsupported params $params"
}

class DataNotFoundException: RepositoryException()