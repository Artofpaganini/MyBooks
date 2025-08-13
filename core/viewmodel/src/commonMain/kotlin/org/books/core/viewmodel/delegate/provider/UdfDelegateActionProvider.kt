package org.books.core.viewmodel.delegate.provider

interface UdfDelegateActionProvider<DelegateAction> {

    suspend fun onAction(action: DelegateAction)
}