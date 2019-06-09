package domain.repositories

import io.photos.domain.entities.Entity
import io.photos.domain.entities.ParamsValidator
import data.exceptions.InvalidRequestParamsException
import data.exceptions.RepositoryException
import io.photos.domain.requests.RequestParams
import io.photos.domain.utils.Either
import io.photos.domain.utils.ResultOk


interface Repository<E: Entity, P: RequestParams> {
    fun create(params: P): Either<ResultOk, RepositoryException>

    fun read(params: P): Either<E, RepositoryException>
}


abstract class AbstractRepository<E: Entity, P: RequestParams>(private val validator: ParamsValidator<P>): Repository<E, P> {

    private fun <R> validateAndExecute(params: P, block: () -> Either<R, RepositoryException>): Either<R, RepositoryException> {
        return validator.validate(params).run {
            if (this is Either.Success) {
                block()
            } else {
                Either.Failure(
                    InvalidRequestParamsException(
                        this::class, params,
                        (this as Either.Failure).error
                    )
                )
            }
        }
    }

    override fun create(params: P): Either<ResultOk, RepositoryException> = validateAndExecute(params) {
        performCreate(params)
    }

    override fun read(params: P): Either<E, RepositoryException> = validateAndExecute(params) {
        performRead(params)
    }

    abstract fun performCreate(params: P): Either<ResultOk, RepositoryException>

    abstract fun performRead(params: P): Either<E, RepositoryException>
}