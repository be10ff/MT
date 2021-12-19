package com.example.mt.ui.main

import android.Manifest
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.mt.data.PermissionStatusListener
import com.example.mt.model.GPSAction
import com.example.mt.model.MainState
import com.example.mt.model.UIAction
import com.example.mt.model.UIState
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application){
    val locationPermissionStatusLiveData = PermissionStatusListener(application, Manifest.permission.ACCESS_FINE_LOCATION)

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


    fun submitAction(action: GPSAction) {
        when(action) {
            is GPSAction.GPSEnabled -> updateState{it.copy(gpsState = true)}
        }
    }

    private fun updateState(update: (MainState) -> MainState) {
        mainState.value?.let{ viewModelScope.launch { mainState.value = update(it) }}
    }
}