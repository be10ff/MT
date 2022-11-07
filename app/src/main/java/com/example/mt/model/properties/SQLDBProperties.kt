package com.example.mt.model.properties

data class SQLDBProperties(
    val zoomingType: ZoomingType,
    val maxZ: Int,
    val minZ: Int,
    val ratio: Int
) {
    fun getLevel(lvl: Int): Int {
        //todo
        return when (zoomingType) {
            ZoomingType.SMART -> lvl
            else -> lvl
        }
    }
}