package com.example.mt.model

import android.location.Location

data class SensorState(
    val location: Location?,
    val orientations: OrientationState
) {
    companion object {
        val InitialState = SensorState(
            null,
            OrientationState( 0f, 0f, 0f)
        )
    }
}
