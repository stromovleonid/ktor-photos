package domain.repositories

import data.exceptions.DataNotFoundException
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
    fun findAll(params: P): Either<List<E>, RepositoryException>
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

    override fun findAll(params: P): Either<List<E>, RepositoryException> = validateAndExecute(params) {
        performFindAll(params)
    }

    abstract fun performCreate(params: P): Either<ResultOk, RepositoryException>

    abstract fun performRead(params: P): Either<E, RepositoryException>

    abstract fun performFindAll(params: P): Either<List<E>, RepositoryException>

    protected fun <T: RequestParams, E> E?.resultOrNotFound(params: T): Either<E, RepositoryException> {
        return if (this != null)
            Either.Success(this)
        else Either.Failure(DataNotFoundException(this@AbstractRepository::class, params))
    }
}