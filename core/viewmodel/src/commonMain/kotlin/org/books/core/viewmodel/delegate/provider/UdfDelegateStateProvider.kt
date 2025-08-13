package org.books.core.viewmodel.delegate.provider

import kotlinx.coroutines.flow.Flow
import org.books.core.viewmodel.annotation.DslDelegateState

@DslDelegateState
interface UdfDelegateStateProvider<DelegateState> {
    fun getDelegateState(): Flow<DelegateState>
}