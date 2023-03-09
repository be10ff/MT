package com.example.mt.model.gi

import android.location.Location

data class GILonLat(
    val lon: Double,
    val lat: Double
) {
    constructor(location: Location) : this(location.longitude, location.latitude)
}
