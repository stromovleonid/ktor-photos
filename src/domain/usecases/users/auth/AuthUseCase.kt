package io.photos.domain.usecases.users.auth

import data.exceptions.DataNotFoundException
import data.exceptions.InvalidRequestParamsException
import data.exceptions.LoginAlreadyTakenException
import domain.repositories.Repository
import io.photos.data.providers.IdProvider
import io.photos.domain.entities.UserEntity
import io.photos.domain.entities.UserMetadataEntity
import io.photos.domain.entities.UsernameEntity
import io.photos.domain.exceptions.*
import io.photos.domain.mappers.Mapper
import io.photos.domain.model.AuthenticatedUserModel
import io.photos.domain.model.UserMetadataModel
import io.photos.domain.requests.AuthRequestParams
import io.photos.domain.requests.UserMetadataRequestParams
import io.photos.domain.usecases.UseCase
import io.photos.domain.utils.AuthTokenProducer
import io.photos.domain.utils.DispatchersProvider
import io.photos.domain.utils.Either
import io.photos.domain.utils.mapBoth

interface AuthUseCase {
    suspend fun performAuth(login: String?, password: String?): Either<AuthenticatedUserModel, UseCaseException>
    suspend fun register(login: String?, password: String?): Either<AuthenticatedUserModel, UseCaseException>
    suspend fun refreshToken(userId: Long?): Either<AuthenticatedUserModel, UseCaseException>
}

class AuthUseCaseImpl(
    dispatchersProvider: DispatchersProvider,
    private val authRepository: Repository<UserEntity, AuthRequestParams>,
    private val metadataRepository: Repository<UserMetadataEntity, UserMetadataRequestParams>,
    private val metadataMapper: Mapper<UserMetadataEntity, UserMetadataModel>,
    private val authTokenProducer: AuthTokenProducer,
    private val idProvider: IdProvider<Long>
) : UseCase(dispatchersProvider), AuthUseCase {

    override suspend fun refreshToken(userId: Long?): Either<AuthenticatedUserModel, UseCaseException> = onIOAsync {
        if (userId == null)
            return@onIOAsync Either.Failure<AuthenticatedUserModel, UseCaseException>(UnauthorizedException)

        val userMetadata =
            metadataRepository.read(UserMetadataRequestParams.FindUserMetadataByIdRequestParams(userId))
        return@onIOAsync userMetadata.mapBoth({
            AuthenticatedUserModel(
                authTokenProducer.produce(userId),
                metadataMapper.toModel(this)
            )
        }) {
            if (this is DataNotFoundException)
                NotFoundException
            else UnknownException
        }
    }

    override suspend fun performAuth(
        login: String?,
        password: String?
    ): Either<AuthenticatedUserModel, UseCaseException> =
        onIOAsync {

            if (login == null || password == null)
                return@onIOAsync Either.Failure<AuthenticatedUserModel, UseCaseException>(UnauthorizedException)

            val result = authRepository.read(AuthRequestParams.LoginRequestParams(login, password))

            if (result is Either.Success) {
                val userMetadata =
                    metadataRepository.read(UserMetadataRequestParams.FindUserMetadataByIdRequestParams(result.result.id))
                return@onIOAsync userMetadata.mapBoth({
                    AuthenticatedUserModel(
                        result.result.getToken(authTokenProducer),
                        metadataMapper.toModel(this)
                    )
                }) {
                    if (this is DataNotFoundException)
                        NotFoundException
                    else UnknownException
                }

            } else
                return@onIOAsync Either.Failure<AuthenticatedUserModel, UseCaseException>(UnauthorizedException)

        }


    override suspend fun register(
        login: String?,
        password: String?
    ): Either<AuthenticatedUserModel, UseCaseException> =
        onIOAsync {

            if (login.isNullOrBlank() || password.isNullOrBlank())
                return@onIOAsync Either.Failure<AuthenticatedUserModel, UseCaseException>(UnauthorizedException)

            val result = authRepository.create(
                AuthRequestParams.RegisterRequestParams(
                    idProvider.provideNext(),
                    login,
                    password
                )
            )

            if (result is Either.Success) {
                val userMetadata =
                    metadataRepository.create(
                        UserMetadataRequestParams.CreateUserMetadataRequestParams(
                            UsernameEntity(
                                login
                            )
                        )
                    )
                return@onIOAsync userMetadata.mapBoth({
                    AuthenticatedUserModel(
                        result.result.getToken(authTokenProducer),
                        metadataMapper.toModel(this)
                    )
                }) {
                    if (this is DataNotFoundException)
                        NotFoundException
                    else UnknownException
                }

            } else
                return@onIOAsync Either.Failure<AuthenticatedUserModel, UseCaseException>(
                    when ((result as Either.Failure).error) {
                        is InvalidRequestParamsException -> InvalidParamsException("$login, $password", result.error)
                        is LoginAlreadyTakenException -> AlreadyTakenException(login, result.error)
                        else -> UnauthorizedException
                    }
                )

        }
}