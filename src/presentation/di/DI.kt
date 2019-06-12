package io.photos.presentation.di

import domain.repositories.Repository
import domain.usecases.users.metadata.UserMetadataUseCase
import domain.usecases.users.metadata.UserMetadataUseCaseImpl
import io.photos.data.providers.IdProvider
import io.photos.data.providers.LongIdProvider
import io.photos.data.repositories.AuthRepository
import io.photos.data.repositories.UserMetadataRepository
import io.photos.domain.AuthConfig
import io.photos.domain.entities.ParamsValidator
import io.photos.domain.entities.UserEntity
import io.photos.domain.entities.UserMetadataEntity
import io.photos.domain.mappers.Mapper
import io.photos.domain.mappers.UserMetadataMapper
import io.photos.domain.model.UserMetadataModel
import io.photos.domain.requests.AuthRequestParams
import io.photos.domain.requests.AuthRequestParamsValidator
import io.photos.domain.requests.UserMetadataRequestParams
import io.photos.domain.requests.UserMetadataRequestParamsValidator
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
                modules(listOf(mappersModule, userMetadataModule, authModule))
            }
    }
}

const val repositoryTag = "_repo"
const val mapperTag = "_mapper"
const val validatorTag = "_validator"

const val metadataTag = "metadata"
const val authTag = "auth"


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
            get(),
            get((named("$metadataTag$validatorTag")))
        )
    }
    factory<IdProvider<Long>> { LongIdProvider() }
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