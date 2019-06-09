package io.photos.repositories

import io.photos.data.providers.LongIdProvider
import io.photos.data.repositories.UserMetadataRepository
import io.photos.domain.requests.UserMetadataRequestParamsValidator

val longIdProvider = LongIdProvider()
val userMetadataRequestsValidator = UserMetadataRequestParamsValidator()
val userMetadataRepository = UserMetadataRepository(longIdProvider, userMetadataRequestsValidator)

val validUsername = "valid"
val invalidUsername = "invalid 12345678901234567890"
