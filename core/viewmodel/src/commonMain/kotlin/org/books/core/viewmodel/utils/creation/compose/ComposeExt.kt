package org.books.core.viewmodel.utils.creation.compose

import androidx.compose.runtime.Composable
import androidx.lifecycle.HasDefaultViewModelProviderFactory
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import org.books.core.viewmodel.host.UdfViewModel

@Composable
@JvmName("udfViewModel")
inline fun <reified Action, UiState, SideEffect> udfViewModel(
    viewModelStoreOwner: ViewModelStoreOwner = checkNotNull(LocalViewModelStoreOwner.current) {
        "No ViewModelStoreOwner was provided via LocalViewModelStoreOwner"
    },
    key: String? = null,
    viewModelFactory: ViewModelProvider.Factory,
    extras: CreationExtras = if (viewModelStoreOwner is HasDefaultViewModelProviderFactory) {
        viewModelStoreOwner.defaultViewModelCreationExtras
    } else {
        CreationExtras.Empty
    }
): UdfViewModel<Action, UiState, SideEffect> = viewModel<UdfViewModel<Action, UiState, SideEffect>>(
    viewModelStoreOwner = viewModelStoreOwner,
    key = key,
    factory = viewModelFactory,
    extras = extras,
)