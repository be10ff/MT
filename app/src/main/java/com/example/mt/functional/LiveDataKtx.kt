package com.example.mt.functional

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData

/**
 * Emits the items that pass through the predicate
 */
inline fun <T> LiveData<T>.filter(crossinline predicate: (T?) -> Boolean): LiveData<T> {
    return MediatorLiveData<T>().apply {
        addSource(this@filter) {
            if (predicate(it)) value = it
        }
    }
}

/**
 * When any of the source [LiveData] emits an item,
 * CombineLatest combines the most recently emitted items from each of the other source [LiveData],
 * using a function you provide, and emits the return value from that function.
 */
fun <F, S, C> LiveData<F>.combineLatest(
    second: LiveData<S>,
    combineFunction: (F?, S?) -> C
): LiveData<C> {
    var (firstEmitted: Boolean, firstValue: F?) = Pair<Boolean, F?>(false, null)
    var (secondEmitted: Boolean, secondValue: S?) = Pair<Boolean, S?>(false, null)

    val runBlock: (MediatorLiveData<C>) -> Unit = { mediator ->
        if (firstEmitted && secondEmitted) {
            mediator.value = combineFunction(firstValue, secondValue)
        }
    }
    return MediatorLiveData<C>().apply {
        addSource(this@combineLatest) { value ->
            firstEmitted = true
            firstValue = value
            runBlock(this)
        }
        addSource(second) { value ->
            secondEmitted = true
            secondValue = value
            runBlock(this)
        }
    }
}