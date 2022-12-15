package com.example.mt.ui.main

import android.graphics.Rect
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mt.model.MainState
import com.example.mt.model.MapState
import com.example.mt.model.UIAction
import com.example.mt.model.gi.Bounds
import com.example.mt.model.gi.Projection
import kotlinx.coroutines.launch

@Deprecated("useless")
class UIViewModel : ViewModel() {
    val mainState: MutableLiveData<MainState> by lazy {
        MutableLiveData<MainState>().apply {
            postValue(
                MainState(
                    gpsState = false,
                    buttonState = false,
                    location = null,
                    storageGranted = false,
                    mapState = MapState(
                        bounds = Bounds(
                            Projection.WGS84,
                            28.0,
                            65.0,
                            48.0,
                            46.0
                        ).reproject(Projection.WorldMercator),
//                        position = Location(),
                        zoom = 1f,
                        viewRect = Rect(),
                        update = false
                    )
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