package com.example.mt.map

import com.example.mt.model.gi.GILonLat
import kotlin.math.*

data class MapUtils(
    val from: GILonLat,
    val to: GILonLat
) {
    val slat = from.lat
    val slon = from.lon
    val flat = to.lat
    val flon = to.lon

    val lat1 = Math.toRadians(slat)
    val lon1 = Math.toRadians(slon)
    val lat2 = Math.toRadians(flat)
    val lon2 = Math.toRadians(flon)

    val cl1 = cos(lat1)
    val cl2 = cos(lat2)
    val sl1 = sin(lat1)
    val sl2 = cos(lat2)

    val delta = lon2 - lon1
    val cdelta = cos(delta)
    val sdelta = sin(delta)

    fun getDistance(): Double {
        val y = hypot(cl2 * sdelta, cl1 * sl2 - sl1 * cl2 * cdelta)
        val x = sl1 * sl2 + cl1 * cl2 * cdelta
        val ad = atan2(y, x)

        val dist = ad * 6372795

        return dist
    }

    fun getAzimuth(): Double {
        val x = cl1 * sl2 - sl1 * cl2 * cdelta
        val y = sdelta * cl2
        val z = if (x >= 0) Math.toDegrees(atan(-y / x))
        else Math.toDegrees(atan(-y / x)) + 180.0
        val z2 = -Math.toRadians(((z + 180.0) % 360.0) - 180.0)
        val anglerad2 = z2 - ((2 * Math.PI) * Math.floor(z2 / (2 * Math.PI)))
        val angledeg = Math.toDegrees(anglerad2)
        return angledeg
    }
}