package com.example.mt.ui.main

import android.Manifest
import android.app.Application
import androidx.lifecycle.*
import com.example.mt.data.MtLocationListener
import com.example.mt.data.PermissionStatusListener
import com.example.mt.model.GPSAction
import com.example.mt.model.MainState
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    val locationPermissionStatusLiveData =
        PermissionStatusListener(application, Manifest.permission.ACCESS_FINE_LOCATION)
    val gpsDataLiveData = MtLocationListener(application)

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
    val gpsState = mainState.map {
        it.location
    }.distinctUntilChanged()


    fun submitAction(action: GPSAction) {
        when (action) {
            is GPSAction.GPSEnabled -> updateState { it.copy(gpsState = true) }
            is GPSAction.LocationUpdated -> {
                updateState { it.copy(location = action.location) }
            }
        }
    }

    private fun updateState(update: (MainState) -> MainState) {
        mainState.value?.let{ viewModelScope.launch { mainState.value = update(it) }}
    }
}