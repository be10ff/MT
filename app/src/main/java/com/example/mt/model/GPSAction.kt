package com.example.mt.model

sealed class GPSAction {
    data class GPSEnabled(val enabled: Boolean) : GPSAction()
}