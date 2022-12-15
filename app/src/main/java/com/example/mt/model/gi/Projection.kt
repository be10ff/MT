package com.example.mt.model.gi

import androidx.collection.arraySetOf
import com.example.mt.map.YandexUtils

sealed class Projection(val name: String) {
    object WGS84 : Projection("WGS84")
    object WorldMercator : Projection("WorldMercator")

    companion object {
        fun reproject(point: GILonLat, source: Projection, destination: Projection): GILonLat =
            when {
                source == WorldMercator && destination == WGS84 -> YandexUtils.mercatorToGeo(point)
                source == WGS84 && destination == WorldMercator -> YandexUtils.geoToMercator(point)
                else -> point
            }

        private val allProjections: Set<Projection> by lazy {
            arraySetOf(WGS84, WorldMercator)
        }

        fun of(name: String): Projection = allProjections.firstOrNull { it.name == name } ?: WGS84
    }
}

//enum class GIProjection(val code: String){
//    WGS84("WGS84"),
//    WorldMercator("WorldMercator")
//
//
//    fun reproject(point: GILonLat, source: GIProjection, destination: GIProjection): GILonLat =
//        when {
//            source == WorldMercator && destination == WGS84 -> GIYandexUtils.mercatorToGeo(point)
//            source == WGS84 && destination == WorldMercator -> GIYandexUtils.geoToMercator(point)
//            else -> point
//        }
//
//}
