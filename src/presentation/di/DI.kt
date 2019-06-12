package io.photos.presentation.di

import domain.repositories.Repository
import domain.usecases.users.metadata.UserMetadataUseCase
import domain.usecases.users.metadata.UserMetadataUseCaseImpl
import io.photos.data.providers.IdProvider
import io.photos.data.providers.LongIdProvider
import io.photos.data.providers.PhotoIdProvider
import io.photos.data.providers.UserIdProvider
import io.photos.data.repositories.AuthRepository
import io.photos.data.repositories.PhotosRepository
import io.photos.data.repositories.UserMetadataRepository
import io.photos.domain.AuthConfig
import io.photos.domain.entities.*
import io.photos.domain.mappers.Mapper
import io.photos.domain.mappers.ParametrizedToModelMapper
import io.photos.domain.mappers.PhotosToModelMapper
import io.photos.domain.mappers.UserMetadataMapper
import io.photos.domain.model.PhotoModel
import io.photos.domain.model.UserMetadataModel
import io.photos.domain.requests.*
import io.photos.domain.usecases.photos.PhotosUseCase
import io.photos.domain.usecases.photos.PhotosUseCaseImpl
import io.photos.domain.usecases.users.auth.AuthUseCase
import io.photos.domain.usecases.users.auth.AuthUseCaseImpl
import io.photos.domain.utils.AuthTokenProducer
import io.photos.domain.utils.AuthTokenProducerImpl
import io.photos.domain.utils.DispatchersProvider
import io.photos.domain.utils.DispatchersProviderImpl
import org.koin.core.context.startKoin
import org.koin.core.qualifier.named
import org.koin.dsl.module
import java.util.concurrent.atomic.AtomicBoolean

object KoinContainer {
    private val isInitialized = AtomicBoolean(false)

    fun init() {
        if (!isInitialized.getAndSet(true))
            startKoin {
                modules(listOf(mappersModule, userMetadataModule, authModule, photosModule))
            }
    }
}

const val repositoryTag = "_repo"
const val mapperTag = "_mapper"
const val validatorTag = "_validator"
const val idProviderTag = "_id_provider"

const val metadataTag = "metadata"
const val authTag = "auth"
const val userTag = "user"
const val photosTag = "photos"


val photosModule = module {
    single<PhotosUseCase> {
        PhotosUseCaseImpl(
            get(), get(named("$photosTag$repositoryTag")),
            get(named("$metadataTag$repositoryTag")),
            get(named("$photosTag$mapperTag")),
            get(named("$metadataTag$mapperTag"))
        )
    }

    single<Repository<PhotoEntity, PhotoRequestParams>>(named("$photosTag$repositoryTag")) {
        PhotosRepository(get(named("$photosTag$idProviderTag")), get(named("$photosTag$validatorTag")))
    }
    single<ParametrizedToModelMapper<PhotoEntity, PhotoModel, UserMetadataModel>>(named("$photosTag$mapperTag")) { PhotosToModelMapper() }
    single<IdProvider<PhotoIdEntity>>(named("$photosTag$idProviderTag")) { PhotoIdProvider(get()) }
    single<ParamsValidator<PhotoRequestParams>>(named("$photosTag$validatorTag")) { PhotoRequestParamsValidator() }
}


val userMetadataModule = module {
    single<UserMetadataUseCase> {
        UserMetadataUseCaseImpl(
            get(), get(named("$metadataTag$repositoryTag")),
            get(named("$metadataTag$mapperTag"))
        )
    }
    single<DispatchersProvider> { DispatchersProviderImpl() }

    single<Repository<UserMetadataEntity, UserMetadataRequestParams>>(named("$metadataTag$repositoryTag")) {
        UserMetadataRepository(
            get(named("$userTag$idProviderTag")),
            get((named("$metadataTag$validatorTag")))
        )
    }
    factory<IdProvider<Long>> { LongIdProvider() }
    single<IdProvider<UserIdEntity>>(named("$userTag$idProviderTag")) { UserIdProvider(get()) }
    single<ParamsValidator<UserMetadataRequestParams>>(named("$metadataTag$validatorTag")) { UserMetadataRequestParamsValidator() }
}

val authModule = module {
    single<AuthUseCase> {
        AuthUseCaseImpl(
            get(),
            get(named("$authTag$repositoryTag")),
            get(named("$metadataTag$repositoryTag")),
            get(named("$metadataTag$mapperTag")),
            get(),
            get()
        )
    }

    single<AuthTokenProducer> {
        val authConfig: AuthConfig = get()
        AuthTokenProducerImpl(authConfig.secret, authConfig.issuer, authConfig.tokenValidTimeMs)
    }

    single<Repository<UserEntity, AuthRequestParams>>(named("$authTag$repositoryTag")) {
        AuthRepository(get((named("$authTag$validatorTag"))))
    }

    single { AuthConfig() }

    single<ParamsValidator<AuthRequestParams>>(named("$authTag$validatorTag")) { AuthRequestParamsValidator() }
}

val mappersModule = module {
    single<Mapper<UserMetadataEntity, UserMetadataModel>>(named("$metadataTag$mapperTag")) {
        UserMetadataMapper()
    }
}