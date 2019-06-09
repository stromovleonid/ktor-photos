package io.photos.domain.usecases

import io.photos.domain.utils.DispatchersProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlin.coroutines.CoroutineContext

abstract class UseCase(private val dispatchersProvider: DispatchersProvider) {

    protected fun <R> onIOAsync(block: UseCase.() -> R) = performAsync(dispatchersProvider.io, block)

    private fun <R> performAsync(context: CoroutineContext, block: UseCase.() -> R): Deferred<R> {
        return CoroutineScope(context).async {
            block()
        }
    }
}