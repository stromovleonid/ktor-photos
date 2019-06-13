package io.photos.domain.usecases.photos

import data.exceptions.DataNotFoundException
import domain.repositories.Repository
import io.ktor.http.content.MultiPartData
import io.photos.domain.entities.PhotoEntity
import io.photos.domain.entities.UserIdEntity
import io.photos.domain.entities.UserMetadataEntity
import io.photos.domain.exceptions.*
import io.photos.domain.mappers.Mapper
import io.photos.domain.mappers.PhotosToModelMapper
import io.photos.domain.model.PhotoModel
import io.photos.domain.model.UserMetadataModel
import io.photos.domain.requests.PhotoRequestParams
import io.photos.domain.requests.UserMetadataRequestParams
import io.photos.domain.usecases.UseCase
import io.photos.domain.utils.*
import java.io.File
import java.util.*

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

    suspend fun uploadPhoto(userIdNullable: Long?, multipart: MultiPartData): Either<PhotoModel, UseCaseException>

    fun getImage(path: String?): Either<File, UseCaseException>
}

class PhotosUseCaseImpl(
    dispatchersProvider: DispatchersProvider,
    private val fileUploader: FileUploader,
    private val imageHost: String,
    private val photosRepository: Repository<PhotoEntity, PhotoRequestParams>,
    private val metadataRepository: Repository<UserMetadataEntity, UserMetadataRequestParams>,
    private val photosMapper: PhotosToModelMapper,
    private val metadataMapper: Mapper<UserMetadataEntity, UserMetadataModel>
) :
    UseCase(dispatchersProvider), PhotosUseCase {


    override fun getImage(path: String?): Either<File, UseCaseException> {
        if (path.isNullOrBlank()) return Either.Failure(NotFoundException)
        val imgFile = File(fileUploader.uploadDir.path + "/" + path)
        if (!imgFile.exists()) return Either.Failure(NotFoundException)
        return Either.Success(imgFile)
    }

    override suspend fun uploadPhoto(
        userIdNullable: Long?,
        multipart: MultiPartData
    ): Either<PhotoModel, UseCaseException> {
        val userId = userIdNullable ?: return Either.Failure(UnauthorizedException)
        val createdAt = System.currentTimeMillis()
        val uploadResult = fileUploader.uploadMultipart(dispatchersProvider, multipart, "${userId}_$createdAt")
        if (uploadResult is Either.Failure) return Either.Failure(FileUploadingException)
        val photoResult = photosRepository.create(
            PhotoRequestParams.UploadPhotoRequestParams(
                UserIdEntity(userId),
                (uploadResult as Either.Success).result,
                Date(createdAt)
            )
        )
        return photoResult.mapBoth({
            val authorMetadata = metadataRepository.read(
                UserMetadataRequestParams.FindUserMetadataByIdRequestParams(userId)
            )

            photosMapper.toModel(
                this,
                metadataMapper.toModel(
                    (authorMetadata as? Either.Success)?.result ?: UserMetadataEntity.MISSING_USER
                ), imageHost
            )
        }) {
            FileUploadingException
        }
    }

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
                        ), imageHost
                    )
                }
            }) {
                if (this is DataNotFoundException)
                    NotFoundException
                else UnknownException
            }
        }
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
                        ), imageHost
                    )
                }
            }) {
                if (this is DataNotFoundException)
                    NotFoundException
                else UnknownException
            }
        }
    }

}