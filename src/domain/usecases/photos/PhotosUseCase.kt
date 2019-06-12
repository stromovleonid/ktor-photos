package io.photos.domain.usecases.photos

import data.exceptions.DataNotFoundException
import domain.repositories.Repository
import io.photos.domain.entities.PhotoEntity
import io.photos.domain.entities.UserIdEntity
import io.photos.domain.entities.UserMetadataEntity
import io.photos.domain.exceptions.InvalidParamsException
import io.photos.domain.exceptions.NotFoundException
import io.photos.domain.exceptions.UnknownException
import io.photos.domain.exceptions.UseCaseException
import io.photos.domain.mappers.Mapper
import io.photos.domain.mappers.ParametrizedToModelMapper
import io.photos.domain.model.PhotoModel
import io.photos.domain.model.UserMetadataModel
import io.photos.domain.requests.PhotoRequestParams
import io.photos.domain.requests.UserMetadataRequestParams
import io.photos.domain.usecases.UseCase
import io.photos.domain.utils.DispatchersProvider
import io.photos.domain.utils.Either
import io.photos.domain.utils.mapBoth

interface PhotosUseCase {
    suspend fun photosOfUser(
        userId: String?,
        pageIndex: String?,
        pageSize: String?
    ): Either<List<PhotoModel>, UseCaseException>

    suspend fun feed(
        pageIndex: String?,
        pageSize: String?
    ): Either<List<PhotoModel>, UseCaseException>
}

class PhotosUseCaseImpl(
    dispatchersProvider: DispatchersProvider,
    val photosRepository: Repository<PhotoEntity, PhotoRequestParams>,
    val metadataRepository: Repository<UserMetadataEntity, UserMetadataRequestParams>,
    val photosMapper: ParametrizedToModelMapper<PhotoEntity, PhotoModel, UserMetadataModel>,
    val metadataMapper: Mapper<UserMetadataEntity, UserMetadataModel>
) :
    UseCase(dispatchersProvider), PhotosUseCase {

    override suspend fun photosOfUser(
        userId: String?,
        pageIndex: String?,
        pageSize: String?
    ): Either<List<PhotoModel>, UseCaseException> {
        return onIOAsync {
            val result = try {
                photosRepository.findAll(
                    PhotoRequestParams.FindPhotosOfUserRequestParams(
                        UserIdEntity(userId!!.toLong()),
                        pageIndex?.toInt() ?: 0,
                        pageSize?.toInt() ?: 20
                    )
                )
            } catch (e: Exception) {
                return@onIOAsync Either.Failure<List<PhotoModel>, UseCaseException>(
                    InvalidParamsException(
                        mapOf(
                            "userId" to userId,
                            "pageIndex" to pageIndex,
                            "pageSize" to pageSize
                        ), e
                    )
                )
            }
            return@onIOAsync result.mapBoth({
                this.map {
                    val authorMetadata = metadataRepository.read(
                        UserMetadataRequestParams.FindUserMetadataByIdRequestParams(it.authorId.id)
                    )

                    return@map photosMapper.toModel(
                        it,
                        metadataMapper.toModel(
                            (authorMetadata as? Either.Success)?.result ?: UserMetadataEntity.MISSING_USER
                        )
                    )
                }
            }) {
                if (this is DataNotFoundException)
                    NotFoundException
                else UnknownException
            }
        }.await()
    }

    override suspend fun feed(pageIndex: String?, pageSize: String?): Either<List<PhotoModel>, UseCaseException> {
        return onIOAsync {
            val result = try {
                photosRepository.findAll(
                    PhotoRequestParams.PhotosFeedRequestParams(
                        pageIndex?.toInt() ?: 0,
                        pageSize?.toInt() ?: 20
                    )
                )
            } catch (e: Exception) {
                return@onIOAsync Either.Failure<List<PhotoModel>, UseCaseException>(
                    InvalidParamsException(
                        mapOf(
                            "pageIndex" to pageIndex,
                            "pageSize" to pageSize
                        ), e
                    )
                )
            }
            return@onIOAsync result.mapBoth({
                this.map {
                    val authorMetadata = metadataRepository.read(
                        UserMetadataRequestParams.FindUserMetadataByIdRequestParams(it.authorId.id)
                    )

                    return@map photosMapper.toModel(
                        it,
                        metadataMapper.toModel(
                            (authorMetadata as? Either.Success)?.result ?: UserMetadataEntity.MISSING_USER
                        )
                    )
                }
            }) {
                if (this is DataNotFoundException)
                    NotFoundException
                else UnknownException
            }
        }.await()
    }

}