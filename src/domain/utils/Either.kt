package io.photos.domain.utils

import io.ktor.http.HttpStatusCode
import io.photos.domain.entities.Entity
import io.photos.domain.exceptions.UseCaseException
import io.photos.domain.mappers.EntityToModelMapper
import io.photos.domain.model.Model

sealed class Either<S, F> {

    data class Success<S, F>(val result: S) : Either<S, F>()

    data class Failure<S, F>(val error: F) : Either<S, F>()
}

fun <S, F : Throwable> Either<S, F>.getOrThrow() =
    if (this is Either.Success)
        this.result
    else
        throw throw (this as Either.Failure).error

private fun <S, F, MS, MF> Either<S, F>.mapBoth(resultMapper: S.() -> MS, failureMapper: F.() -> MF): Either<MS, MF> {
    return if (this is Either.Success) Either.Success(this.result.resultMapper())
    else Either.Failure((this as Either.Failure).error.failureMapper())
}

fun <S : Entity, F, MS : Model, MF> Either<S, F>.mapToModel(
    mapper: EntityToModelMapper<S, MS>,
    failureMapper: F.() -> MF
): Either<MS, MF> {
    return mapBoth({ mapper.toModel(this) }, failureMapper)
}

fun <S : Entity, F, MS : Model, MF> Either<List<S>, F>.mapListToModel(
    mapper: EntityToModelMapper<S, MS>,
    failureMapper: F.() -> MF
): Either<List<MS>, MF> {
    return mapBoth({ map { mapper.toModel(it) } }, failureMapper)
}

fun <S, F, MF> Either<S, F>.mapFailure(failureMapper: F.() -> MF): Either<S, MF> {
    return mapBoth({ this }, failureMapper)
}


fun <S : Any, F : UseCaseException> Either<S, F>.toApiResponse(): Any {
    return if (this is Either.Success) this.result
    else (this as Either.Failure).error.apiMessage
}

fun <S : Any, F : UseCaseException> Either<S, F>.getApiResponseCode(): HttpStatusCode {
    return if (this is Either.Success) HttpStatusCode.OK
    else (this as Either.Failure).error.apiCode
}
