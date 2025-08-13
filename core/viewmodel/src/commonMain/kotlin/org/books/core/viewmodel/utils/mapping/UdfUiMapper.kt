package org.books.core.viewmodel.utils.mapping

import org.books.core.viewmodel.annotation.DslState

fun interface UdfUiMapper<@DslState State, UiState> {

    fun invoke(state: State): UiState
}