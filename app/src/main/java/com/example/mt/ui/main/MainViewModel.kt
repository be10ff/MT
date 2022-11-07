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
    val readStoragePermissionStatusLiveData =
        PermissionStatusListener(application, Manifest.permission.READ_EXTERNAL_STORAGE)
    val gpsDataLiveData = MtLocationListener(application)

    val mainState: MutableLiveData<MainState> by lazy {
        MutableLiveData<MainState>().apply {
            postValue(
                MainState(
                    gpsState = false,
                    buttonState = false,
                    location = null,
                    storageGranted = false
                )
            )
        }
    }
    val gpsState = mainState.map {
        it.location
    }.distinctUntilChanged()

    val storageState = mainState.map {
        it.storageGranted
    }.distinctUntilChanged()


    fun submitAction(action: GPSAction) {
        when (action) {
            is GPSAction.GPSEnabled -> updateState { it.copy(gpsState = true) }

            is GPSAction.LocationUpdated -> {
                updateState { it.copy(location = action.location) }
            }

            is GPSAction.StorageGranted -> {
                updateState { it.copy(storageGranted = action.granted) }
            }
        }
    }

    private fun updateState(update: (MainState) -> MainState) {
        mainState.value?.let{ viewModelScope.launch { mainState.value = update(it) }}
    }
}