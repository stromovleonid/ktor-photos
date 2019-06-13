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
import java.lang.Exception


interface UserMetadataUseCase {

    suspend fun create(username: String): Either<ResultOk, UseCaseException>

    suspend fun findAll(
        query: String?,
        ignoreCase: String?,
        pageIndex: String?,
        pageSize: String?
    ): Either<List<UserMetadataModel>, UseCaseException>

    suspend fun findById(id: String?): Either<UserMetadataModel, UseCaseException>
}


class UserMetadataUseCaseImpl(
    dispatchersProvider: DispatchersProvider,
    private val repository: Repository<UserMetadataEntity, UserMetadataRequestParams>,
    private val metadataMapper: Mapper<UserMetadataEntity, UserMetadataModel>
) : UseCase(dispatchersProvider), UserMetadataUseCase {

    override suspend fun create(username: String): Either<ResultOk, UseCaseException> {
        return onIOAsync {
            val result = repository.create(
                UserMetadataRequestParams.CreateUserMetadataRequestParams(
                    UsernameEntity(username)
                )
            )
            result.mapBoth({ ResultOk }) {
                if (this is ValidationException)
                    ModelValidationException(this)
                else UnknownException
            }
        }
    }

    override suspend fun findAll(
        query: String?,
        ignoreCase: String?,
        pageIndex: String?,
        pageSize: String?
    ): Either<List<UserMetadataModel>, UseCaseException> {
        return onIOAsync {
            val result = try {
                repository.findAll(
                    UserMetadataRequestParams.FindUserMetadataRequestParams(
                        query.orEmpty(),
                        ignoreCase?.toBoolean() ?: true,
                        pageIndex?.toInt() ?: 0,
                        pageSize?.toInt() ?: 20
                    )
                )
            } catch (e: Exception) {
                return@onIOAsync Either.Failure<List<UserMetadataModel>, UseCaseException>(
                    InvalidParamsException(
                        mapOf(
                            "query" to query,
                            "ignoreCase" to ignoreCase,
                            "pageIndex" to pageIndex,
                            "pageSize" to pageSize
                        ), e
                    )
                )
            }

            return@onIOAsync result.mapListToModel(metadataMapper) {
                if (this is DataNotFoundException)
                    NotFoundException
                else UnknownException
            }
        }
    }

    override suspend fun findById(id: String?): Either<UserMetadataModel, UseCaseException> {
        return onIOAsync {

            val longId = try {
                id!!.toLong()
            } catch (e: KotlinNullPointerException) {
                return@onIOAsync Either.Failure<UserMetadataModel, UseCaseException>(InvalidParamsException(id, e))
            } catch (e: NumberFormatException) {
                return@onIOAsync Either.Failure<UserMetadataModel, UseCaseException>(
                    ParseParamsException(
                        id,
                        Long::class
                    )
                )
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
        }
    }
}