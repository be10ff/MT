package com.example.mt.model

sealed class TrackState {
    object Stopped : TrackState()
    data class Started(val fileName: String) : TrackState()
}
