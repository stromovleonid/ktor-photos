package domain.usecases.users.metadata

import domain.repositories.Repository
import io.photos.domain.entities.UserMetadataEntity
import io.photos.domain.entities.UsernameEntity
import data.exceptions.DataNotFoundException
import io.photos.domain.exceptions.NotFoundException
import io.photos.domain.exceptions.UnknownException
import io.photos.domain.exceptions.UseCaseException
import data.exceptions.ValidationException
import io.photos.domain.exceptions.ModelValidationException
import io.photos.domain.mappers.Mapper
import io.photos.domain.model.UserMetadataModel
import io.photos.domain.model.UsernameModel
import io.photos.domain.requests.UserMetadataRequestParams
import io.photos.domain.usecases.UseCase
import io.photos.domain.utils.*
import kotlinx.coroutines.Deferred

class UserMetadataUseCase(
    dispatchersProvider: DispatchersProvider,
    private val repository: Repository<UserMetadataEntity, UserMetadataRequestParams>,
    private val usernameMapper: Mapper<UsernameEntity, UsernameModel>,
    private val metadataMapper: Mapper<UserMetadataEntity, UserMetadataModel>
) : UseCase(dispatchersProvider) {

    fun createAsync(username: UsernameModel): Deferred<Either<ResultOk, UseCaseException>> {
        return onIOAsync {
            val result = repository.create(
                UserMetadataRequestParams.CreateUserMetadataRequestParams(
                    usernameMapper.toEntity(username)
                )
            )
            result.mapFailure {
                if (this is ValidationException)
                    ModelValidationException(this)
                else UnknownException
            }
        }
    }

    fun findByIdAsync(id: Long): Deferred<Either<UserMetadataModel, UseCaseException>> {
        return onIOAsync {
            val result = repository.read(
                UserMetadataRequestParams.FindUserMetadataByIdRequestParams(
                    id
                )
            )

            result.mapEntity(metadataMapper) {
                if (this is DataNotFoundException)
                    NotFoundException
                else UnknownException
            }
        }
    }
}