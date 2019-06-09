package io.photos.domain.exceptions

import data.exceptions.ValidationException

open class UseCaseException: Exception()

object NotFoundException: UseCaseException()

object UnknownException: UseCaseException()

class ModelValidationException(original: ValidationException): UseCaseException(){
    override val cause = original
}