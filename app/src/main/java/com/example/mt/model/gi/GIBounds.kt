package com.example.mt.model.gi

data class GIBounds(
    val projection: GIProjection,
    val left: Double,
    val top: Double,
    val right: Double,
    val bottom: Double
) {
    fun height() = top - bottom
    fun width() = right - left

    fun intesect(bounds: GIBounds): Boolean {
        TODO("Not yet implemented")
    }

    fun contains(bounds: GIBounds): Boolean {
        TODO("Not yet implemented")
    }

    fun leftTop() = GILonLat(left, top)

    fun rightBottom() = GILonLat(right, bottom)
}
