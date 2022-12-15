package com.example.mt.model.gi

import com.example.mt.map.YandexUtils
import com.example.mt.model.xml.GILayerType
import kotlin.math.cos
import kotlin.math.ln
import kotlin.math.tan

sealed class GITile(
    val x: Int,
    val y: Int,
    val z: Int,
    layerType: GILayerType
) {
    class GISQLYandexTile(x: Int, y: Int, z: Int) : GITile(x, y, z, GILayerType.SQL_LAYER)

    val bounds: Bounds = Bounds(
        Projection.WGS84,
        YandexUtils.tile2lon(x, z),
        YandexUtils.tile2lat(y, z),
        YandexUtils.tile2lon(x + 1, z),
        YandexUtils.tile2lat(y + 1, z)
    )

    companion object {
        val tilePx = 256

        fun create(z: Int, lon: Double, lat: Double, type: GILayerType): GITile {
            return when (type) {
                //osm title
                GILayerType.SQL_LAYER -> {
                    GISQLYandexTile(
                        x = ((lon + 180) / 360 * (1 shl z)).toInt(),
                        y = ((1 - ln(tan(Math.toRadians(lat)) + 1 / cos(Math.toRadians(lat))) / Math.PI) / 2 * (1 shl z)).toInt(),
                        z = z
                    )
                }
                else -> {
                    GISQLYandexTile(
                        x = ((lon + 180) / 360 * (1 shl z)).toInt(),
                        y = ((1 - ln(tan(Math.toRadians(lat)) + 1 / cos(Math.toRadians(lat))) / Math.PI) / 2 * (1 shl z)).toInt(),
                        z = z
                    )
                }
            }
        }
    }
}