package org.books.core.viewmodel.delegate

import org.books.core.viewmodel.delegate.provider.UdfDelegateActionProvider
import org.books.core.viewmodel.delegate.provider.UdfDelegateSideEffectProvider
import org.books.core.viewmodel.delegate.provider.UdfDelegateStateProvider

@JvmSuppressWildcards
interface UdfDelegate<DelegateAction, DelegateState, DelegateSideEffect>
    : UdfDelegateActionProvider<DelegateAction>,
    UdfDelegateStateProvider<DelegateState>,
    UdfDelegateSideEffectProvider<DelegateSideEffect>