package org.books.core.viewmodel.utils.observe.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import org.books.core.viewmodel.provider.UdfSideEffectProvider

@Composable
inline fun <reified SideEffect : Any> UdfSideEffectProvider<SideEffect>.collectSideEffect(
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    lifecycleState: Lifecycle.State = Lifecycle.State.STARTED,
    noinline handleSideEffect: (effect: SideEffect) -> Unit
) {
    val callback by rememberUpdatedState(newValue = handleSideEffect)
    LaunchedEffect(key1 = getSideEffect(), key2 = lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(lifecycleState) {
            getSideEffect().collect { value -> callback(value) }
        }
    }
}