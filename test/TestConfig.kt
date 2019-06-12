import domain.repositories.Repository
import domain.usecases.users.metadata.UserMetadataUseCase
import io.photos.data.providers.IdProvider
import io.photos.domain.entities.ParamsValidator
import io.photos.domain.entities.PhotoEntity
import io.photos.domain.entities.UserEntity
import io.photos.domain.entities.UserMetadataEntity
import io.photos.domain.requests.AuthRequestParams
import io.photos.domain.requests.PhotoRequestParams
import io.photos.domain.requests.UserMetadataRequestParams
import io.photos.domain.usecases.photos.PhotosUseCase
import io.photos.domain.usecases.users.auth.AuthUseCase
import io.photos.presentation.di.*
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.koin.core.qualifier.named

const val validUsername = "valid"
const val secondValidUsername = "second"
const val invalidUsername = "invalid 12345678901234567890"

object Dependencies: KoinComponent {

    init {
        KoinContainer.init()
    }

    val userMetadataUseCase by inject<UserMetadataUseCase>()
    val userMetadataRepository by inject<Repository<UserMetadataEntity, UserMetadataRequestParams>>(named("$metadataTag$repositoryTag"))
    val longIdProvider by inject<IdProvider<Long>>()
    val userMetadataRequestsValidator by inject<ParamsValidator<UserMetadataRequestParams>>(named("$metadataTag$validatorTag"))

    val authParamsValidator by inject<ParamsValidator<AuthRequestParams>>(named("$authTag$validatorTag"))
    val authUseCase by inject<AuthUseCase>()
    val authRepository by inject<Repository<UserEntity, AuthRequestParams>>(named("$authTag$repositoryTag"))

    val photosUseCase by inject<PhotosUseCase>()
    val photosRepository  by inject<Repository<PhotoEntity, PhotoRequestParams>>(named("$photosTag$repositoryTag"))
    val photoParamsValidator by inject<ParamsValidator<PhotoRequestParams>>(named("$photosTag$validatorTag"))
}
