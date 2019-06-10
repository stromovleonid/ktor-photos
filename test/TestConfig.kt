import domain.repositories.Repository
import domain.usecases.users.metadata.UserMetadataUseCase
import io.photos.data.providers.IdProvider
import io.photos.domain.entities.ParamsValidator
import io.photos.domain.entities.UserMetadataEntity
import io.photos.domain.requests.UserMetadataRequestParams
import io.photos.presentation.di.KoinContainer
import org.koin.core.KoinComponent
import org.koin.core.inject

const val validUsername = "valid"
const val secondValidUsername = "second"
const val invalidUsername = "invalid 12345678901234567890"

object Dependencies: KoinComponent {

    init {
        KoinContainer.init()
    }

    val userMetadataUseCase by inject<UserMetadataUseCase>()
    val userMetadataRepository by inject<Repository<UserMetadataEntity, UserMetadataRequestParams>>()
    val longIdProvider by inject<IdProvider<Long>>()
    val userMetadataRequestsValidator by inject<ParamsValidator<UserMetadataRequestParams>>()
}
