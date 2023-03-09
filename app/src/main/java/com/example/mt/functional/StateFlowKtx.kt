package com.example.mt.functional

import androidx.lifecycle.LifecycleCoroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class StateFlowKtx

fun <T> Flow<T>.launchWhenStarted(lifecycleScope: LifecycleCoroutineScope) {
    lifecycleScope.launchWhenStarted {
        this@launchWhenStarted.collect()
    }
}

fun <T> MutableStateFlow<T>.updateFlow(lifecycleScope: CoroutineScope, update: (T) -> T) {
    lifecycleScope.launch { this@updateFlow.value = update(this@updateFlow.value) }
}

