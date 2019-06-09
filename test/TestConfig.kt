import domain.usecases.users.metadata.UserMetadataUseCase
import io.photos.data.providers.LongIdProvider
import io.photos.data.repositories.UserMetadataRepository
import io.photos.domain.mappers.UserMetadataMapper
import io.photos.domain.mappers.UsernameMapper
import io.photos.domain.requests.UserMetadataRequestParamsValidator
import io.photos.domain.utils.DispatchersProviderImpl

val dispatchersProvider = DispatchersProviderImpl()

val usernameMapper = UsernameMapper()
val userMetadataMapper = UserMetadataMapper(usernameMapper)

val longIdProvider = LongIdProvider()
val userMetadataRequestsValidator = UserMetadataRequestParamsValidator()
val userMetadataRepository = UserMetadataRepository(longIdProvider, userMetadataRequestsValidator)
val userMetadataUseCase = UserMetadataUseCase(
    dispatchersProvider,
    userMetadataRepository, usernameMapper, userMetadataMapper
)

val validUsername = "valid"
val invalidUsername = "invalid 12345678901234567890"
