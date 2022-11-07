package com.example.mt.model.gi

data class GIBounds(
    val projection: GIProjection,
    val left: Double,
    val top: Double,
    val right: Double,
    val bottom: Double
) {
    val topLeft = GILonLat(left, top)
    val bottomRight = GILonLat(right, bottom)
    val height = top - bottom
    val width = right - left

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

    fun reproject(projection: GIProjection): GIBounds {
        val topLeft = GIProjection.reproject(this.topLeft, this.projection, projection)
        val bottomRight = GIProjection.reproject(bottomRight, this.projection, projection)
        return GIBounds(projection, topLeft.lon, topLeft.lat, bottomRight.lon, bottomRight.lat)
    }

    companion object
}
