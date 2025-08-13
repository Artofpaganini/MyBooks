package org.books.core.viewmodel.delegate

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import org.books.core.viewmodel.annotation.DslDelegateSideEffect
import org.books.core.viewmodel.annotation.DslDelegateState
import org.books.core.viewmodel.logStage
import org.books.core.viewmodel.side_effect_wrapper.SideEffectStreamWrapper
import org.books.core.viewmodel.state_wrapper.StateStreamWrapper
import kotlin.reflect.KProperty

abstract class UdfBaseDelegate<DelegateAction, DelegateState, DelegateSideEffect>(
    initialState: () -> DelegateState,
) : UdfDelegate<DelegateAction, DelegateState, DelegateSideEffect> {

    private val vmDelegateTag = this@UdfBaseDelegate::class.java.simpleName

    private val stateStreamWrapper = StateStreamWrapper(initialState.invoke())

    val delegateState by stateStreamWrapper.getStream()

    private val sideEffectWrapper by lazy { SideEffectStreamWrapper<DelegateSideEffect?>(vmDelegateTag) }

    override suspend fun onAction(action: DelegateAction) {
        vmDelegateTag.logStage(
            methodName = "onAction",
            message = "Выполнить Action ${action?.let { new -> new::class.java.simpleName }}"
        )
    }

    override fun getDelegateState(): Flow<DelegateState> = stateStreamWrapper.getStream()
        .filterNotNull()

    override fun getDelegateSideEffect(): Flow<DelegateSideEffect> = sideEffectWrapper.getStream()
        .filterNotNull()

    @DslDelegateState
    protected fun updateDelegateState(block: @DslDelegateState DelegateState.() -> DelegateState) {
        stateStreamWrapper updateStateBy block
    }

    @DslDelegateSideEffect
    protected fun postDelegateSideEffect(newEffect: @DslDelegateSideEffect DelegateSideEffect) {
        sideEffectWrapper post newEffect
    }

    @DslDelegateSideEffect
    protected fun postOnReturnDelegateSideEffect(
        newEffect: @DslDelegateSideEffect DelegateSideEffect,
        condition: () -> Boolean
    ) {
        sideEffectWrapper.postOnReturnSideEffect(newEffect, condition)
    }

    @DslDelegateSideEffect
    protected fun handleDelegateSideEffect() {
        sideEffectWrapper.handle()
    }

    private operator fun <T> StateFlow<T>.getValue(thisRef: Any?, property: KProperty<*>): T = this.value
}
