package org.books.core.viewmodel.utils.mapping

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import org.books.core.viewmodel.annotation.DslState
import org.books.core.viewmodel.annotation.DslValueHolder
import kotlin.reflect.KProperty

@DslValueHolder
fun <StateItem : Any?> stateValue(initialValue: StateItem): Lazy<StateValueHolder<StateItem>> =
    lazy { StateValueHolder(initialValue) }

@DslState
class StateValueHolder<StateItem>(item: StateItem) {

    private val mutableState = mutableStateOf(item)

    val value: StateItem by mutableState

    val state: State<StateItem> = mutableState

    @DslValueHolder
    fun updateTo(block: @DslValueHolder StateItem.() -> StateItem) {
        block.invoke(mutableState.value)
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