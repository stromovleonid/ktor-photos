package io.photos.data.repositories

import data.exceptions.RepositoryException
import domain.repositories.AbstractRepository
import io.photos.domain.entities.ParamsValidator
import io.photos.domain.entities.UserEntity
import io.photos.domain.requests.AuthRequestParams
import io.photos.domain.utils.Either
import io.photos.domain.utils.ResultOk

class AuthRepository(validator: ParamsValidator<AuthRequestParams>):
    AbstractRepository<UserEntity, AuthRequestParams>(validator) {

    private val users = mutableListOf<UserEntity>().apply {
        add(UserEntity(1001L, "test", "test"))
    }

    override fun performCreate(params: AuthRequestParams): Either<ResultOk, RepositoryException> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun performRead(params: AuthRequestParams): Either<UserEntity, RepositoryException> =
        users.find { it.login == params.login && it.password == params.password }
            .resultOrNotFound(params)

    override fun performFindAll(params: AuthRequestParams) = throw UnsupportedOperationException()

}