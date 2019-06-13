package io.photos.domain.usecases

import io.photos.domain.utils.DispatchersProvider
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

abstract class UseCase(protected val dispatchersProvider: DispatchersProvider) {

    protected suspend fun <R> onIOAsync(block: UseCase.() -> R) = performAsync(dispatchersProvider.io, block)

    private suspend fun <R> performAsync(context: CoroutineContext, block: UseCase.() -> R): R {
        return withContext(context + SupervisorJob()) {
            block()
        }
    }
}