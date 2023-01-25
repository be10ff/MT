package com.example.mt.model

import android.location.Location

data class MainState(
    val gpsState: Boolean,
    val buttonState: ButtonState,
    val location: Location?,
    val storageGranted: Boolean,
    val mapState: MapState
)
