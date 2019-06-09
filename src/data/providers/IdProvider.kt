package io.photos.data.providers

import java.util.concurrent.atomic.AtomicLong

interface IdProvider<ID> {
    fun provideNext(): ID
    fun current(): ID
}

class LongIdProvider: IdProvider<Long> {
    private val ids = AtomicLong()

    override fun provideNext() = ids.incrementAndGet()

    override fun current() = ids.get()
}