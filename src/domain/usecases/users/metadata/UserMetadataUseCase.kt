package domain.usecases.users.metadata

import domain.repositories.Repository
import io.photos.domain.entities.UserMetadataEntity
import io.photos.domain.entities.UsernameEntity
import data.exceptions.DataNotFoundException
import data.exceptions.ValidationException
import io.photos.domain.exceptions.*
import io.photos.domain.mappers.Mapper
import io.photos.domain.model.UserMetadataModel
import io.photos.domain.requests.UserMetadataRequestParams
import io.photos.domain.usecases.UseCase
import io.photos.domain.utils.*

class UserMetadataUseCase(
    dispatchersProvider: DispatchersProvider,
    private val repository: Repository<UserMetadataEntity, UserMetadataRequestParams>,
    private val metadataMapper: Mapper<UserMetadataEntity, UserMetadataModel>
) : UseCase(dispatchersProvider) {

    suspend fun create(username: String): Either<ResultOk, UseCaseException> {
        return onIOAsync {
            val result = repository.create(
                UserMetadataRequestParams.CreateUserMetadataRequestParams(
                    UsernameEntity(username)
                )
            )
            result.mapFailure {
                if (this is ValidationException)
                    ModelValidationException(this)
                else UnknownException
            }
        }.await()
    }

    suspend fun findById(id: String?): Either<UserMetadataModel, UseCaseException> {
        return onIOAsync {

            val longId = try {
                id!!.toLong()
            } catch(e: KotlinNullPointerException) {
                return@onIOAsync Either.Failure<UserMetadataModel, UseCaseException>(InvalidParamsException(id))
            } catch (e: NumberFormatException) {
                return@onIOAsync Either.Failure<UserMetadataModel, UseCaseException>(ParseParamsException(id, Long::class))
            }

            val result = repository.read(
                UserMetadataRequestParams.FindUserMetadataByIdRequestParams(
                    longId
                )
            )

            return@onIOAsync result.mapToModel(metadataMapper) {
                if (this is DataNotFoundException)
                    NotFoundException
                else UnknownException
            }
        }.await()
    }
}