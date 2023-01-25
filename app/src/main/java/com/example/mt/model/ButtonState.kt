package com.example.mt.model

data class ButtonState(
    val follow: Boolean,
    val showTrack: Boolean,
    val writeTrack: Boolean,

    val editGeometry: Boolean,
    val deleteGeometry: Boolean

) {
    companion object {
        val InitialState = ButtonState(
            follow = false,
            showTrack = false,
            writeTrack = false,
            editGeometry = false,
            deleteGeometry = false
        )
    }

}
