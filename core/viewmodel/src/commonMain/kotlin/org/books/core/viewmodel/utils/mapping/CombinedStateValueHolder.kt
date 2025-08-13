package org.books.core.viewmodel.utils.mapping

import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import org.books.core.viewmodel.annotation.DslValueHolder
import kotlin.reflect.KProperty

@DslValueHolder
fun <StateItem : Any, UiStateItem : Any> stateValue(
    initialValue: StateItem,
    mapper: StateItem.() -> UiStateItem
): Lazy<CombinedStateValueHolder<StateItem, UiStateItem>> = lazy { CombinedStateValueHolder(initialValue, mapper) }

@DslValueHolder
class CombinedStateValueHolder<StateItem : Any, UiStateItem : Any>(
    initialItem: StateItem,
    private val toUiStateItem: StateItem.() -> UiStateItem
) {
    private val mutableState = mutableStateOf(initialItem)
    private val mutableUiState = mutableStateOf(mutableState.value.toUiStateItem())

    val value: StateItem by mutableState

    val uiState: State<UiStateItem> = derivedStateOf { value.toUiStateItem() }

    @DslValueHolder
    fun updateTo(block: @DslValueHolder StateItem.() -> StateItem) {
        val newValue = block.invoke(mutableState.value)
        mutableUiState.value = newValue.toUiStateItem()
    }

    @DslValueHolder
    fun updateTo(newValue: @DslValueHolder StateItem) {
        if (mutableState.value == newValue) return
        mutableState.value = newValue
        mutableUiState.value = newValue.toUiStateItem()
    }

    private operator fun <StateItem> State<StateItem>.getValue(thisRef: Any?, property: KProperty<*>): StateItem =
        this.value
}