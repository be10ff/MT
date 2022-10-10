package com.example.mt.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mt.model.MainState
import com.example.mt.model.UIAction
import kotlinx.coroutines.launch

@Deprecated("useless")
class UIViewModel : ViewModel() {
    val mainState: MutableLiveData<MainState> by lazy {
        MutableLiveData<MainState>().apply {
            postValue(
                MainState(
                    gpsState = false,
                    buttonState = false,
                    location = null
                )
            )
        }
    }

    fun submitAction(action: UIAction) {
        when (action) {
            is UIAction.ToggleGps -> updateState { it.copy(gpsState = !it.gpsState) }
            is UIAction.ToggleTracking -> updateState { it.copy(buttonState = !it.buttonState) }
//            is UIAction.LocationUpdated -> {
//                updateState{it.copy(location = action.location)}
//            }
            else -> {}
        }.also {
            mainState.value?.location
        }
    }

    private fun updateState(update: (MainState) -> MainState) {
        mainState.value?.let{ viewModelScope.launch { mainState.value = update(it) }}
    }


}