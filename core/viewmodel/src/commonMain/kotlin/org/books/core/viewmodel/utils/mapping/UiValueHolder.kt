package org.books.core.viewmodel.utils.mapping

import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import org.books.core.viewmodel.annotation.DslValueHolder

@DslValueHolder
fun <StateItem : Any?, UiStateItem : Any?> transformUiValueFrom(
    stateHolder: StateValueHolder<StateItem>,
    mapper: StateItem.() -> UiStateItem,
): Lazy<UiValueHolder<StateItem, UiStateItem>> = lazy {
    UiValueHolder(stateHolder = stateHolder, mapper = mapper,)
}

@DslValueHolder
class UiValueHolder<StateItem : Any?, UiStateItem : Any?>(
    stateHolder: StateValueHolder<StateItem>,
    mapper: StateItem.() -> UiStateItem,
) {
    val uiState: State<UiStateItem> = derivedStateOf {
        stateHolder.state.value.mapper()
    }
}
