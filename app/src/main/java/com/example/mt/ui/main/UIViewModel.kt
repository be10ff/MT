package com.example.mt.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mt.model.UIAction
import com.example.mt.model.MainState
import kotlinx.coroutines.launch

class UIViewModel : ViewModel() {
    val mainState: MutableLiveData<MainState> by lazy {
        MutableLiveData<MainState>().apply {
            postValue(
                MainState(
                    gpsState = false,
                    buttonState = false
                )
            )
        }
    }

    fun submitAction(action: UIAction) {
        when(action) {
            is UIAction.ToggleMenu -> updateState{it.copy(buttonState = !it.buttonState)}
        }
    }

    private fun updateState(update: (MainState) -> MainState) {
        mainState.value?.let{ viewModelScope.launch { mainState.value = update(it) }}
    }



}