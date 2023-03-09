package com.example.mt.model

data class ButtonState(
    val follow: Boolean,
    val showTrack: Boolean,
    val writeTrack: TrackState,

    val editGeometry: Boolean,
    val deleteGeometry: Boolean

) {
    companion object {
        val InitialState = ButtonState(
            follow = false,
            showTrack = false,
            writeTrack = TrackState.Stopped,
            editGeometry = false,
            deleteGeometry = false
        )
    }

}
