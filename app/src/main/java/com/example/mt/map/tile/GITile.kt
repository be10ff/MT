package com.example.mt.map.tile

import com.example.mt.model.gi.Bounds
import com.example.mt.model.gi.Projection
import com.example.mt.model.xml.GILayerType
import kotlin.math.pow

sealed class GITile(
    val x: Int,
    val y: Int,
    val z: Int
) {

    abstract fun tile2lat(y: Int, z: Int): Double

    abstract fun url(): String

    fun tile2lon(x: Int, z: Int): Double {
        return x / 2.0.pow(z) * 360 - 180
    }

    val bounds: Bounds = Bounds(
        Projection.WGS84,
        tile2lon(x, z),
        tile2lat(y, z),
        tile2lon(x + 1, z),
        tile2lat(y + 1, z)
    )

    companion object {
        const val tilePx = 256

        fun create(z: Int, lon: Double, lat: Double, type: GILayerType): GITile {
            return when (type) {
                //osm title
                GILayerType.SQL_LAYER -> GIOSMTile.from(z, lon, lat)

                GILayerType.SQL_YANDEX_LAYER -> GISQLYandexTile.from(z, lon, lat)

                else -> GIOSMTile.from(z, lon, lat)
            }
        }

//        abstract fun from(z: Int, lon: Double, lat: Double) :GITile
    }
}