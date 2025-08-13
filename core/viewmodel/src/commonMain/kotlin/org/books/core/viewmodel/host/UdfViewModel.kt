package org.books.core.viewmodel.host

import androidx.lifecycle.ViewModel
import org.books.core.viewmodel.provider.UdfContentProvider

abstract class UdfViewModel<Action, UiState, SideEffect> : ViewModel(), UdfContentProvider<UiState, SideEffect> {

    abstract fun onAction(action: Action)

}