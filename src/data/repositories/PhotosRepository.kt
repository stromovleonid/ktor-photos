package io.photos.data.repositories

import data.exceptions.RepositoryException
import data.exceptions.UnsupportedRequestParamsException
import domain.repositories.AbstractRepository
import io.photos.data.providers.IdProvider
import io.photos.domain.entities.*
import io.photos.domain.requests.PhotoRequestParams
import io.photos.domain.utils.Either
import java.util.*
import kotlin.random.Random

class PhotosRepository(
    private val idProvider: IdProvider<PhotoIdEntity>,
    validator: ParamsValidator<PhotoRequestParams>
) :
    AbstractRepository<PhotoEntity, PhotoRequestParams>(validator) {

    private val photos = mutableListOf<PhotoEntity>().apply {
        val random = Random.Default

        repeat(100) {
            val w = random.nextInt(100, 1000)
            val h = random.nextInt(100, 1000)
            add(
                PhotoEntity(
                    idProvider.provideNext(),
                    UserIdEntity(random.nextLong(0, 1000)),
                    "https://picsum.photos/$w/$h",
                    PhotoMetadataEntity(w, h, Date())
                )
            )
        }

        repeat(100) {
            val w = random.nextInt(100, 1000)
            val h = random.nextInt(100, 1000)
            add(
                PhotoEntity(
                    idProvider.provideNext(),
                    UserIdEntity(1001L),
                    "https://picsum.photos/$w/$h",
                    PhotoMetadataEntity(w, h, Date())
                )
            )
        }
    }

    override fun performCreate(params: PhotoRequestParams): Either<PhotoEntity, RepositoryException> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun performRead(params: PhotoRequestParams): Either<PhotoEntity, RepositoryException> {
        throw UnsupportedOperationException()
    }

    override fun performFindAll(params: PhotoRequestParams): Either<List<PhotoEntity>, RepositoryException> {
        return when (params) {
            is PhotoRequestParams.FindPhotosOfUserRequestParams -> {
                findAll(params.pageIndex, params.pageSize) { it.authorId == params.authorId }
            }

            is PhotoRequestParams.PhotosFeedRequestParams -> {
                findAll(params.pageIndex, params.pageSize) { true }
            }

            else -> throw UnsupportedRequestParamsException(params)
        }
    }

    private fun findAll(pageIndex: Int, pageSize: Int, predicate: (PhotoEntity) -> Boolean): Either<List<PhotoEntity>, RepositoryException>  {
        val fromIndex = pageIndex * pageSize
        val toIndex = (pageIndex + 1) * pageSize

        if (fromIndex >= photos.size) return Either.Success(emptyList())

        val results = photos.filter(predicate)
        val toIndexTrimmed = if (toIndex <= results.size) toIndex else results.size
        val fromIndexTrimmed = if (fromIndex > toIndexTrimmed) toIndexTrimmed else fromIndex
        return Either.Success(
            results
                .subList(fromIndexTrimmed, toIndexTrimmed)
        )
    }
}