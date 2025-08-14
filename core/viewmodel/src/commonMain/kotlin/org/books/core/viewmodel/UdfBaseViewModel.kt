package org.books.core.viewmodel

import androidx.annotation.AnyThread
import androidx.annotation.MainThread
import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.plus
import org.books.core.viewmodel.annotation.DslSideEffect
import org.books.core.viewmodel.annotation.DslState
import org.books.core.viewmodel.annotation.DslUdfBaseViewModel
import org.books.core.viewmodel.delegate.UdfDelegate
import org.books.core.viewmodel.delegate.provider.UdfDelegateActionProvider
import org.books.core.viewmodel.host.UdfViewModel
import org.books.core.viewmodel.side_effect_wrapper.SideEffectStreamWrapper
import org.books.core.viewmodel.state_wrapper.StateStreamWrapper
import org.books.core.viewmodel.utils.dispatchers.UdfDispatchers
import org.books.core.viewmodel.utils.flow.launchInJob
import org.books.core.viewmodel.utils.flow.launchJob
import org.books.core.viewmodel.utils.mapping.UdfUiMapper
import kotlin.reflect.KProperty
import androidx.compose.runtime.State as ComposeState

typealias OnDelegateSideEffect<DelegateEffect, SideEffect, DelegateAction> = (
        ((DelegateEffect) -> SideEffect) -> Unit,
        (() -> DelegateAction) -> Unit,
) -> Unit

/**
 * Весь флоу настройки/принципа работы, описан на Wiki
 * Раздел - "Работа с UdfViewModel. Архитектурный подход - Unidirectional data flow (UDF)"
 *
 * Запрещается добавлять любые переменные, а так же делать изменения.
 * Любые изменения этой ViewModel, должны быть согласованы с лидом.
 */

abstract class UdfBaseViewModel<Action, UiState, SideEffect, State>(
    private val initialState: () -> State,
    private val mapper: (State) -> UdfUiMapper<State, UiState>,
    private val dispatchers: UdfDispatchers,
) : UdfViewModel<Action, UiState, SideEffect>() {

    private val vmName = this@UdfBaseViewModel::class.java.simpleName

    private val stateWrapper = StateStreamWrapper(initialState.invoke())

    private val sideEffectWrapper by lazy { SideEffectStreamWrapper<SideEffect?>(vmName) }

    val state by stateWrapper.getStream()

    private val internalMapper = mapper.invoke(initialState.invoke())

    @MainThread
    override fun onAction(action: Action) {
        vmName.logStage(
            methodName = "onAction",
            message = "Выполнить Action ${action?.let { new -> new::class.java.simpleName }}"
        )
    }

    @MainThread
    override fun getSideEffect(): Flow<SideEffect> = sideEffectWrapper.getStream().filterNotNull()

    @NonRestartableComposable
    @Composable
    override fun collectUiState(): ComposeState<UiState> = remember(stateWrapper) {
        stateWrapper.getStream()
            .mapOn(internalMapper::invoke)
            .stateIn(
                scope = viewModelScope + dispatchers.map,
                started = SharingStarted.WhileSubscribed(),
                initialValue = internalMapper.invoke(initialState.invoke())
            )
    }.collectAsState()

    @AnyThread
    override fun getUiState(): Flow<UiState> = stateWrapper.getStream()
        .mapOn(internalMapper::invoke)
        .flowOn(dispatchers.map)


    @DslState
    protected fun updateState(block: @DslState State.() -> State) {
        stateWrapper updateStateBy block
    }

    @DslSideEffect
    protected fun postSideEffect(effect: @DslSideEffect SideEffect) {
        sideEffectWrapper.post(effect)
    }

    @DslSideEffect
    protected fun postOnReturnSideEffect(
        effect: @DslSideEffect SideEffect,
        condition: () -> Boolean
    ) {
        sideEffectWrapper.postOnReturnSideEffect(effect, condition)
    }

    @DslSideEffect
    protected fun handleSideEffect() {
        sideEffectWrapper.handle()
    }

    /**
     * Не забывайте отменять подписку при уходе с экрана. В частности, в случаях, которые приводят пересозданию активити
     */
    protected fun <DelegateAction, DelegateState, DelegateEffect> UdfDelegate<DelegateAction, DelegateState, DelegateEffect>.observeDelegate(
        coroutineDispatcher: CoroutineDispatcher? = null,
        onState: (DelegateState.() -> Unit)? = null,
        onStateError: (suspend (Throwable) -> Unit)? = null,
        onSideEffect: OnDelegateSideEffect<DelegateEffect, SideEffect, DelegateAction>? = null,
        onSideEffectError: (suspend (Throwable) -> Unit)? = null,
        onError: ((Throwable) -> Unit)? = null
    ): Job = viewModelScope.launchJob(
        catchBlock = { throwable -> onError?.invoke(throwable) },
        context = (coroutineDispatcher ?: dispatchers.work),
    ) {
        if (onState == null && onSideEffect == null) cancel()
        onState?.let { stateCallback ->
            getDelegateState()
                .filterNotNull()
                .onEach(stateCallback::invoke)
                .launchInJob(
                    catchBlock = { throwable -> onStateError?.invoke(throwable) ?: throwable.printStackTrace() },
                    scope = this
                )
        }
        onSideEffect?.let { sideEffectCallback ->
            getDelegateSideEffect()
                .filterNotNull()
                .onEach { delegateEffect ->
                    sideEffectCallback.invoke(
                        { parentEffect -> postSideEffect(parentEffect.invoke(delegateEffect)) },
                        { handle -> delegateActionInScope(handle.invoke()) }
                    )
                }.launchInJob(
                    catchBlock = { throwable -> onSideEffectError?.invoke(throwable) ?: throwable.printStackTrace() },
                    scope = this
                )
        }
    }

    @DslUdfBaseViewModel
    protected fun withScope(
        coroutineDispatcher: CoroutineDispatcher? = null,
        onError: ((Throwable) -> Unit)? = null,
        block: suspend () -> Unit
    ): Job = viewModelScope.launchJob(
        catchBlock = { throwable -> onError?.invoke(throwable) ?: throwable.printStackTrace() },
        context = (coroutineDispatcher ?: dispatchers.work)
    ) {
        block.invoke()
    }

    @DslUdfBaseViewModel
    protected fun <T> UdfDelegateActionProvider<T>.delegateActionInScope(
        action: T,
        coroutineDispatcher: CoroutineDispatcher? = null,
        onError: ((Throwable) -> Unit)? = null
    ): Job = viewModelScope.launchJob(
        catchBlock = { throwable -> onError?.invoke(throwable) ?: throwable.printStackTrace() },
        context = (coroutineDispatcher ?: dispatchers.work)
    ) {
        onAction(action)
    }

    @DslUdfBaseViewModel
    protected fun <T> UdfDelegateActionProvider<T>.delegateActionsInScope(
        vararg actions: T,
        coroutineDispatcher: CoroutineDispatcher? = null,
        onError: ((Throwable) -> Unit)? = null
    ): Job = viewModelScope.launchJob(
        catchBlock = { throwable -> onError?.invoke(throwable) ?: throwable.printStackTrace() },
        context = (coroutineDispatcher ?: dispatchers.work)
    ) {
        actions.forEach { action -> onAction(action) }
    }

    @AnyThread
    private fun <State> StateFlow<State>.mapOn(
        applyChanges: (State) -> UiState
    ): Flow<UiState> = if (dispatchers.map != Dispatchers.Main.immediate) {
        map { applyChanges.invoke(it) }.flowOn(dispatchers.map)
    } else {
        map(applyChanges::invoke)
    }

    private operator fun <T> StateFlow<T>.getValue(thisRef: Any?, property: KProperty<*>): T = this.value
}

internal fun String.logStage(methodName: String, message: String) {
    println("$this:$methodName $message")
}
