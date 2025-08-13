package org.books.core.viewmodel.utils.dispatchers

import kotlinx.coroutines.CoroutineDispatcher

data class UdfDispatchers(
    val map: CoroutineDispatcher,
    val work: CoroutineDispatcher,
)

fun udfDispatchers(
    map: CoroutineDispatcher,
    work: CoroutineDispatcher,
): UdfDispatchers = UdfDispatchers(map, work)