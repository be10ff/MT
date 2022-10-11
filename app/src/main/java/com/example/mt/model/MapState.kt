package com.example.mt.model

import android.location.Location

data class MapState(
    val position: Location,
    val zoom: Float
)

data class Bounds(
    val left: Double,
    val top: Double,
    val right: Double,
    val bottom: Double
)
