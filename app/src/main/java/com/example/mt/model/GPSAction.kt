package com.example.mt.model

import android.location.Location

sealed class GPSAction {
    data class GPSEnabled(val enabled: Boolean) : GPSAction()
    data class LocationUpdated(val location: Location) : GPSAction()
    data class StorageGranted(val granted: Boolean) : GPSAction()
}