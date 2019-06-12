package io.photos.data.repositories

import data.exceptions.IdAlreadyTakenException
import data.exceptions.InvalidRequestParamsException
import data.exceptions.LoginAlreadyTakenException
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

    override fun performCreate(params: AuthRequestParams): Either<UserEntity, RepositoryException> {
        params as AuthRequestParams.RegisterRequestParams
        if (users.find { it.id == params.id } != null)
            return Either.Failure(IdAlreadyTakenException(this::class, params))

        if (users.find { it.login == params.login } != null)
            return Either.Failure(LoginAlreadyTakenException(this::class, params))

        val newUser = UserEntity(params.id, params.login, params.password)
        users.add(newUser)
        return Either.Success(newUser)
    }

    override fun performRead(params: AuthRequestParams): Either<UserEntity, RepositoryException> {
        params as AuthRequestParams.LoginRequestParams
        return users.find { it.login == params.login && it.password == params.password }
            .resultOrNotFound(params)
    }


    override fun performFindAll(params: AuthRequestParams) = throw UnsupportedOperationException()

}