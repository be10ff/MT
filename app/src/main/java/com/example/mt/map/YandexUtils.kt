package com.example.mt.map

import com.example.mt.model.gi.GILonLat
import kotlin.math.*

class YandexUtils {

    companion object {
        val R: Double = 6378137.0
        val Le: Double = 40075000.0
        val Lg: Double = 20003930.0
        val latitude_deg = Lg / 180

        fun geoToMercator(point: GILonLat): GILonLat {
            val d = Math.toRadians(point.lon)
            val m = Math.toRadians(point.lat)
            val k = 0.0818191908426
            val f = k * sin(m)
            val h = tan(Math.PI / 4 + m / 2)
            val j = tan(Math.PI / 4 + asin(f) / 2).pow(k)
            val i = h / j

            return GILonLat(R * d, R * ln(i))
        }

        fun mercatorToGeo(point: GILonLat): GILonLat {
            val n = 0.003356551468879694
            val k = 0.00000657187271079536
            val h = 1.764564338702e-8
            val m = 5.328478445e-11

            val g = Math.PI / 2 - 2 * atan(1 / exp(point.lat / R))
            val l = g + n * sin(2 * g) + k * sin(4 * g) + h * sin(6 * g) + m * sin(8 * g)
            val d = point.lon / R

            return GILonLat(Math.toDegrees(d), Math.toDegrees(l))
        }
//Moved to
//        fun tile2lon(x: Int, z: Int) = x / 2.0.pow(z) * 360 - 180
//        fun tile2lat(y: Int, z: Int) = Math.toDegrees(
//            atan(
//                sinh(
//                    Math.PI - 2 * Math.PI * y / 2.0.pow(
//                        z
//                    )
//                )
//            )
//        )
    }
}