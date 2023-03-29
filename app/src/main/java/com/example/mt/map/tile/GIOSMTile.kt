package com.example.mt.map.tile

import kotlin.math.*

//GITileInfoOSM
open class GIOSMTile(x: Int, y: Int, z: Int) : GITile(x, y, z) {

//   constructor(z: Int, lon: Double, lat: Double) : this(
//       x = ((lon + 180) / 360 * (1 shl z)).toInt(),
//       y = ((1 - ln(tan(Math.toRadians(lat)) + 1 / cos(Math.toRadians(lat))) / Math.PI) / 2 * (1 shl z)).toInt(),
//       z = z
//   )

//    fun tile2lon(x: Int, z: Int): Double {
//        return x / 2.0.pow(z) * 360 - 180
//    }

    override fun tile2lat(y: Int, z: Int): Double = Math.toDegrees(
        atan(
            sinh(
                Math.PI - 2 * Math.PI * y / 2.0.pow(z)
            )
        )
    )

//    override fun url(): String = "http://a.tile.openstreetmap.org/$z/$x/$y.png"

    companion object {
        fun from(z: Int, lon: Double, lat: Double): GIOSMTile {
            val x = ((lon + 180) / 360 * (1 shl z)).toInt()
                .let { _x ->
                    when {
                        _x < 0 -> 0
                        _x >= 1 shl z -> (1 shl z) - 1
                        else -> _x
                    }
                }
            val y =
                ((1 - ln(tan(Math.toRadians(lat)) + 1 / cos(Math.toRadians(lat))) / Math.PI) / 2 * (1 shl z)).toInt()
                    .let { _y ->
                        when {
                            _y < 0 -> 0
                            _y >= 1 shl z -> (1 shl z) - 1
                            else -> _y
                        }
                    }

            return GIOSMTile(
                x = x,
                y = y,
                z = z
            )
        }
    }

}