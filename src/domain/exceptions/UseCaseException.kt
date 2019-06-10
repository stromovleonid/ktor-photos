package io.photos.domain.exceptions

import data.exceptions.ValidationException
import kotlin.reflect.KClass

abstract class UseCaseException: Exception() {

    abstract val apiMessage: String

    override fun toString() = apiMessage
}

object NotFoundException: UseCaseException() {
    override val apiMessage = "Not found"
}

object UnknownException: UseCaseException() {
    override val apiMessage = "UnknownException"
}

class ModelValidationException(original: ValidationException): UseCaseException(){
    override val apiMessage = "Validation error, ${original.message}"

    override val cause = original
}

class InvalidParamsException(params: Any?): UseCaseException(){
    override val apiMessage = "Invalid params $params"

    override val message = "Invalid params $params"
}

class ParseParamsException(params: Any?, expectedClass: KClass<*>): UseCaseException(){
    override val apiMessage = "Invalid params $params, expected to be ${expectedClass.simpleName}"

    override val message = "Params $params cannot be parsed as ${expectedClass.simpleName}"
}