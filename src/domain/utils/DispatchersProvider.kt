package io.photos.domain.utils

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

interface DispatchersProvider {
    val default: CoroutineDispatcher
    val io: CoroutineDispatcher
}

class DispatchersProviderImpl: DispatchersProvider {
    override val default: CoroutineDispatcher
        get() = Dispatchers.Default
    override val io: CoroutineDispatcher
        get() = Dispatchers.IO
}