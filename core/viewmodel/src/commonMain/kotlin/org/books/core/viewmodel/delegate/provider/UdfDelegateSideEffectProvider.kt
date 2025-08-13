package org.books.core.viewmodel.delegate.provider

import kotlinx.coroutines.flow.Flow
import org.books.core.viewmodel.annotation.DslDelegateSideEffect

@DslDelegateSideEffect
interface UdfDelegateSideEffectProvider<DelegateSideEffect> {
    fun getDelegateSideEffect(): Flow<DelegateSideEffect>
}