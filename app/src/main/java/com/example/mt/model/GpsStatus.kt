package com.example.mt.model

import com.example.mt.R

sealed class GpsStatus {
    data class Enabled(val message: Int = R.string.gps_status_enabled) : GpsStatus()
    data class Disabled(val message: Int = R.string.gps_status_disabled) : GpsStatus()
}
