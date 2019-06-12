package io.photos.domain.usecases.users.auth

import data.exceptions.DataNotFoundException
import domain.repositories.Repository
import domain.usecases.users.metadata.UserMetadataUseCase
import io.photos.data.repositories.UserMetadataRepository
import io.photos.domain.entities.UserEntity
import io.photos.domain.entities.UserMetadataEntity
import io.photos.domain.exceptions.NotFoundException
import io.photos.domain.exceptions.UnauthorizedException
import io.photos.domain.exceptions.UnknownException
import io.photos.domain.exceptions.UseCaseException
import io.photos.domain.mappers.Mapper
import io.photos.domain.mappers.UserMetadataMapper
import io.photos.domain.model.AuthenticatedUserModel
import io.photos.domain.model.UserMetadataModel
import io.photos.domain.requests.AuthRequestParams
import io.photos.domain.requests.UserMetadataRequestParams
import io.photos.domain.usecases.UseCase
import io.photos.domain.utils.*

interface AuthUseCase {
    suspend fun performAuth(login: String?, password: String?): Either<AuthenticatedUserModel, UseCaseException>
}

class AuthUseCaseImpl(
    dispatchersProvider: DispatchersProvider,
    private val authRepository: Repository<UserEntity, AuthRequestParams>,
    private val metadataRepository: Repository<UserMetadataEntity, UserMetadataRequestParams>,
    private val metadataMapper: Mapper<UserMetadataEntity, UserMetadataModel>,
    private val authTokenProducer: AuthTokenProducer
) : UseCase(dispatchersProvider), AuthUseCase {

    override suspend fun performAuth(
        login: String?,
        password: String?
    ): Either<AuthenticatedUserModel, UseCaseException> =
        onIOAsync {

            if (login == null || password == null)
                return@onIOAsync Either.Failure<AuthenticatedUserModel, UseCaseException>(UnauthorizedException)

            val result = authRepository.read(AuthRequestParams(login, password))

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

        }.await()
}