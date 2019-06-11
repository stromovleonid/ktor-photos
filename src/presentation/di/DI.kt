package io.photos.presentation.di

import domain.repositories.Repository
import domain.usecases.users.metadata.UserMetadataUseCase
import io.photos.data.providers.IdProvider
import io.photos.data.providers.LongIdProvider
import io.photos.data.repositories.UserMetadataRepository
import io.photos.domain.entities.ParamsValidator
import io.photos.domain.entities.UserMetadataEntity
import io.photos.domain.mappers.Mapper
import io.photos.domain.mappers.UserMetadataMapper
import io.photos.domain.model.UserMetadataModel
import io.photos.domain.requests.UserMetadataRequestParams
import io.photos.domain.requests.UserMetadataRequestParamsValidator
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
                modules(listOf(mappersModule, userMetadataModule))
            }
    }
}

val userMetadataModule = module {
    single {
        UserMetadataUseCase(
            get(), get(),
            get(named("userMetadataMapper"))
        )
    }
    single<DispatchersProvider> { DispatchersProviderImpl() }

    single<Repository<UserMetadataEntity, UserMetadataRequestParams>> { UserMetadataRepository(get(), get()) }
    factory<IdProvider<Long>> { LongIdProvider() }
    single<ParamsValidator<UserMetadataRequestParams>> { UserMetadataRequestParamsValidator() }
}

val mappersModule = module {
    single<Mapper<UserMetadataEntity, UserMetadataModel>>(named("userMetadataMapper")) {
        UserMetadataMapper()
    }
}