package org.books.core.viewmodel.provider

import kotlinx.coroutines.flow.Flow
import org.books.core.viewmodel.annotation.DslSideEffect

@DslSideEffect
interface UdfSideEffectProvider<SideEffect> {
    fun getSideEffect(): Flow<SideEffect>
}