package com.example.mt.model

import android.location.Location

data class MainState(
    val gpsState: Boolean,
    val buttonState: ButtonState,
    val location: Location?,
    val manageGranted: Boolean
) {
    companion object {
        val InitialState = MainState(
            gpsState = false,
            buttonState = ButtonState.InitialState,
            location = null,
            manageGranted = false
        )
    }
}
