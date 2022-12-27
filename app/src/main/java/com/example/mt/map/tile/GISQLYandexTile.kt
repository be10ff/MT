package com.example.mt.map.tile

import com.example.mt.model.gi.GILonLat
import com.example.mt.model.gi.Projection
import kotlin.math.*

//GITileInfoYandex
open class GISQLYandexTile(x: Int, y: Int, z: Int) : GITile(x, y, z) {

    override fun tile2lat(y: Int, z: Int): Double {
        val merkElipsK = 0.0000001
        val sradiusa = 6378137
        val sradiusb = 6356752
        val fExct = sqrt(sradiusa.toDouble() * sradiusa - sradiusb.toDouble() * sradiusb) / sradiusa
        val tilesAtZoom = 1 shl z
        val result01 = (y - tilesAtZoom / 2) / -(tilesAtZoom / (2 * Math.PI))
        val result02 = (2 * atan(Math.exp(result01)) - Math.PI / 2) * 180 / Math.PI
        var zu = result02 / (180 / Math.PI)
        val yy = y - tilesAtZoom / 2
        var zum1 = zu
        zu = asin(
            1 - ((1 + sin(zum1)) * Math.pow(
                1 - fExct * sin(zum1),
                fExct
            )) / (exp((2 * yy) / -(tilesAtZoom / (2 * Math.PI))) * (1 + fExct * sin(
                zum1
            )).pow(fExct))
        )
        while (Math.abs(zum1 - zu) >= merkElipsK) {
            zum1 = zu
            zu = asin(
                1 - ((1 + sin(zum1)) * Math.pow(
                    1 - fExct * sin(zum1),
                    fExct
                )) / (exp((2 * yy) / -(tilesAtZoom / (2 * Math.PI))) * (1 + fExct * sin(
                    zum1
                )).pow(fExct))
            )
        }
        val result = zu * 180 / Math.PI
        return result
    }

    override fun url(): String {
        val unixTime = System.currentTimeMillis() / 1000
        return "http://jgo.maps.yandex.net/1.1/tiles?l=trf,trfl&lang=ru_RU&x={$x}&y={$y}&z={$z}&tm={$unixTime}"
    }

    companion object {
        fun from(z: Int, lon: Double, lat: Double): GISQLYandexTile {
            val point =
                Projection.reproject(GILonLat(lon, lat), Projection.WGS84, Projection.WorldMercator)
            val (x, y) = getTile(point, z)
            return GISQLYandexTile(
                x = x,
                y = y,
                z = z
            )
        }

        fun getTile(point: GILonLat, z: Int): Pair<Int, Int> {
            val mercator = mercatorToTiles(point.lon to point.lat)
            val res = getTile(mercator, z)
            return res
        }

        fun mercatorToTiles(from: Pair<Double, Double>): Pair<Int, Int> {
            val d = ((20037508.342789 + from.first) * 53.5865938).roundToInt()
                .let {
                    boundaryRestrict(it, 0, 2147483647)
                }
            val f = ((20037508.342789 - from.second) * 53.5865938).roundToInt()
                .let {
                    boundaryRestrict(it, 0, 2147483647)
                }
            return d to f
        }

        fun getTile(h: Pair<Int, Int>, z: Int): Pair<Int, Int> {
            val e = 8
            val j = toScale(z)
            val g = h.first shr j
            val f = h.second shr j

            return (g shr e) to (f shr e)
        }

        fun toScale(z: Int): Int = 23 - z

        fun boundaryRestrict(f: Double, e: Double, d: Double): Double = max(min(f, d), e)
        fun boundaryRestrict(f: Long, e: Long, d: Long): Long = max(min(f, d), e)
        fun boundaryRestrict(f: Int, e: Int, d: Int): Int = max(min(f, d), e)
    }
}