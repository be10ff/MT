package com.example.mt.model.gi

sealed class GIProjection {
    object WGS84 : GIProjection()
    object WorldMercator : GIProjection()

    companion object {
        fun reproject(point: GILonLat, source: GIProjection, destination: GIProjection): GILonLat =
            when {
                source == WorldMercator && destination == WGS84 -> GIYandexUtils.mercatorToGeo(point)
                source == WGS84 && destination == WorldMercator -> GIYandexUtils.geoToMercator(point)
                else -> point
            }
    }
}
