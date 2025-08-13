package org.books.core.viewmodel.utils.flow

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext

fun <T> Flow<T>.launchInJob(
    scope: CoroutineScope,
    catchBlock: suspend (t: Throwable) -> Unit = Throwable::printStackTrace,
): Job =
    this.catch { throwable -> catchBlock.invoke(throwable) }
        .launchIn(scope)

fun CoroutineScope.launchJob(
    catchBlock: (t: Throwable) -> Unit,
    context: CoroutineContext,
    finallyBlock: (() -> Unit)? = null,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    tryBlock: suspend CoroutineScope.() -> Unit
): Job {
    return launch(context + BaseCoroutineExceptionHandler(catchBlock), start = start) {
        try {
            tryBlock()
        } finally {
            finallyBlock?.invoke()
        }
    }
}

private class BaseCoroutineExceptionHandler(
    private val errorCallback: ((Throwable) -> Unit)
) : AbstractCoroutineContextElement(CoroutineExceptionHandler), CoroutineExceptionHandler {
    override fun handleException(context: CoroutineContext, exception: Throwable) {
        errorCallback(exception)
    }
}
