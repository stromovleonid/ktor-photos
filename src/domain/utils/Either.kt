package io.photos.domain.utils

sealed class Either<S, F> {

    data class Success<S, F>(val result: S) : Either<S, F>()

    data class Failure<S, F>(val error: F) : Either<S, F>()
}

fun <S, F : Throwable> Either<S, F>.getOrThrow() =
    if (this is Either.Success)
        this.result
    else
        throw throw (this as Either.Failure).error