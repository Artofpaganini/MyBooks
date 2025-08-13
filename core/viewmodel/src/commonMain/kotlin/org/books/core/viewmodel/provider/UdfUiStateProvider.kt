package org.books.core.viewmodel.provider

import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.State
import kotlinx.coroutines.flow.Flow
import org.books.core.viewmodel.annotation.DslState

@DslState
interface UdfUiStateProvider<UiState> {

    @NonRestartableComposable
    @Composable
    fun collectUiState(): State<UiState>

    fun getUiState(): Flow<UiState>
}