package org.books.core.viewmodel.utils.mapping

import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import org.books.core.viewmodel.annotation.DslValueHolder
import kotlin.reflect.KProperty

@DslValueHolder
fun <StateItem : Any?, UiStateItem : Any?> stateHolder(
    initialValue: StateItem,
    mapper: StateItem.() -> UiStateItem,
): Lazy<StateValueHolder<StateItem, UiStateItem>> =
    lazy { StateValueHolder(initialValue, mapper) }

@DslValueHolder
class StateValueHolder<StateItem, UiStateItem>(
    item: StateItem,
    mapper: StateItem.() -> UiStateItem,
) {

    private val mutableState = mutableStateOf(item)

    val value: StateItem by mutableState

    val uiState: State<UiStateItem> = derivedStateOf {
        mutableState.value.mapper()
    }

    @DslValueHolder
    fun updateTo(block: @DslValueHolder StateItem.() -> StateItem) {
        mutableState.value = block.invoke(mutableState.value)
    }

    @DslValueHolder
    fun updateTo(newValue: @DslValueHolder StateItem) {
        if (mutableState.value == newValue) return
        mutableState.value = newValue
    }

    private operator fun <StateItem> State<StateItem>.getValue(
        thisRef: Any?,
        property: KProperty<*>,
    ): StateItem = this.value
}