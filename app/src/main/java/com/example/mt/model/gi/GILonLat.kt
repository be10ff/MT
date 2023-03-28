package com.example.mt.model.gi

import android.location.Location
import kotlin.math.floor

data class GILonLat(
    val lon: Double,
    val lat: Double,
    val projection: Projection
) {
    constructor(location: Location) : this(location.longitude, location.latitude, Projection.WGS84)

    override fun toString(): String {
        return coordString(this).first + " " + coordString(this).second
    }
    companion object {

        fun coordString(coord: Double): String {
            val dergees = floor(coord).toInt()
            val mins = floor((coord - dergees) * 60).toInt()
            val secs = ((coord - dergees) * 60 - mins) * 60
            return String.format("%d° %d\' %.4f\"", dergees, mins, secs)
        }

        fun coordString(lonLat: GILonLat): Pair<String, String> {
            return Projection.reproject(lonLat, Projection.WGS84).run{
                coordString(lon)  to coordString(lat)
            }
        }
    }
}
