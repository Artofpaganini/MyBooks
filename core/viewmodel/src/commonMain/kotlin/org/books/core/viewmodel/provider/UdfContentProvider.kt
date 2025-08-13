package org.books.core.viewmodel.provider

interface UdfContentProvider<UiState, SideEffect> :
    UdfUiStateProvider<UiState>,
    UdfSideEffectProvider<SideEffect>
