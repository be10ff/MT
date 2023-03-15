package com.example.mt.model.gi

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

//    fun leftTop() = GILonLat(left, top)
//
//    fun rightBottom() = GILonLat(right, bottom)

    fun reproject(projection: Projection): Bounds {
        val topLeft = Projection.reproject(this.topLeft, this.projection, projection)
        val bottomRight = Projection.reproject(bottomRight, this.projection, projection)
        return Bounds(projection, topLeft.lon, topLeft.lat, bottomRight.lon, bottomRight.lat)
    }

    fun contains(point: GILonLat): Boolean {
        return Projection.reproject(point, Projection.WGS84, projection)
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
