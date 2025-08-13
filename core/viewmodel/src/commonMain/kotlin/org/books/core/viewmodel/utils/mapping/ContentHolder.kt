package org.books.core.viewmodel.utils.mapping

import java.util.concurrent.atomic.AtomicReference

fun <StateItem : Any, UiStateItem : Any> contentHolder(
    initialContent: StateItem,
    mapper: StateItem.() -> UiStateItem
): Lazy<ContentHolder<StateItem, UiStateItem>> = lazy { ContentHolder(initialContent, mapper) }

class ContentHolder<StateItem : Any, UiStateItem : Any>(
    initialItem: StateItem,
    private val mapper: StateItem.() -> UiStateItem
) {
    private val state = AtomicReference(Holder(initialItem, initialItem.mapper()))

    val stateValue: StateItem get() = state.get().value

    val uiValue: UiStateItem get() = state.get().uiValue

    fun updateAndGet(newValue: StateItem): UiStateItem {
        val current = state.get()
        if (current.value == newValue) return current.uiValue
        val newState = Holder(newValue, newValue.mapper())
        state.set(newState)
        return newState.uiValue
    }

    fun forceUpdate(newValue: StateItem) {
        val newState = Holder(newValue, newValue.mapper())
        state.set(newState)
    }

    private data class Holder<out StateItem : Any, out UiStateItem : Any>(
        val value: StateItem,
        val uiValue: UiStateItem
    )
}

fun <T : Any> contentHolderHelper(
    initialHelper: T,
): Lazy<ContentHolderHelper<T>> = lazy { ContentHolderHelper(initialHelper) }

class ContentHolderHelper<T : Any>(initialHelper: T) {
    private val state = AtomicReference(Holder(initialHelper))

    val value: T get() = state.get().value

    infix fun updateTo(newValue: T) {
        val current = state.get()
        if (current.value == newValue) return
        val newState = Holder(newValue)
        state.set(newState)
    }

    private data class Holder<out T : Any>(
        val value: T,
    )
}