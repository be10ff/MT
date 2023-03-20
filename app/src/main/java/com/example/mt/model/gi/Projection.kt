package com.example.mt.model.gi

import androidx.collection.arraySetOf
import com.example.mt.map.YandexUtils

sealed class Projection(val name: String) {
    object WGS84 : Projection("WGS84")
    object WorldMercator : Projection("WorldMercator")

    companion object {
        fun reproject(point: GILonLat, destination: Projection): GILonLat =
            when {
                point.projection == WorldMercator && destination == WGS84 -> YandexUtils.mercatorToGeo(
                    point
                )
                point.projection == WGS84 && destination == WorldMercator -> YandexUtils.geoToMercator(
                    point
                )
                else -> point
            }

        private val allProjections: Set<Projection> by lazy {
            arraySetOf(WGS84, WorldMercator)
        }

        fun of(name: String): Projection = allProjections.firstOrNull { it.name == name } ?: WGS84
    }
}

