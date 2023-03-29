package com.example.mt.model.gi

import android.app.Application
import android.util.Log
import android.widget.Toast

data class Bounds(
    val projection: Projection,
    val left: Double,
    val top: Double,
    val right: Double,
    val bottom: Double
) {
    val topLeft = GILonLat(left, top, projection)
    val bottomRight = GILonLat(right, bottom, projection)
    val height = top - bottom
    val width = right - left
    val center = GILonLat((left + right) / 2, (top + bottom) / 2, projection)

//    fun intesect(bounds: GIBounds): Boolean {
//        TODO("Not yet implemented")
//    }
//
//    fun contains(bounds: GIBounds): Boolean {
//        TODO("Not yet implemented")
//    }


    fun reproject(projection: Projection): Bounds {
        return if (this.projection != projection) {
            val topLeft = Projection.reproject(topLeft, projection)
            val bottomRight = Projection.reproject(bottomRight, projection)
            Bounds(projection, topLeft.lon, topLeft.lat, bottomRight.lon, bottomRight.lat)
        } else this
    }

    fun contains(point: GILonLat): Boolean {
        return Projection.reproject(point, projection)
            .let {
                it.lon in left..right && it.lat in bottom..top
            }

    }


    companion object {
        val InitialState = Bounds(
            Projection.WGS84,
            28.0,
            65.0,
            48.0,
            46.0
        ).reproject(Projection.WorldMercator)
    }
}
