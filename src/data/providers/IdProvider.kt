package io.photos.data.providers

import io.photos.domain.entities.PhotoIdEntity
import io.photos.domain.entities.UserIdEntity
import java.util.concurrent.atomic.AtomicLong

interface IdProvider<ID> {
    fun provideNext(): ID
    fun current(): ID
}

class UserIdProvider(private val longIdProvider: IdProvider<Long>) : IdProvider<UserIdEntity> {
    override fun provideNext() = UserIdEntity(longIdProvider.provideNext())

    override fun current() = UserIdEntity(longIdProvider.current())
}

class PhotoIdProvider(private val longIdProvider: IdProvider<Long>) : IdProvider<PhotoIdEntity> {
    override fun provideNext() = PhotoIdEntity(longIdProvider.provideNext())

    override fun current() = PhotoIdEntity(longIdProvider.current())
}

class LongIdProvider : IdProvider<Long> {
    private val ids = AtomicLong(0)

    override fun provideNext() = ids.incrementAndGet()

    override fun current() = ids.get()
}