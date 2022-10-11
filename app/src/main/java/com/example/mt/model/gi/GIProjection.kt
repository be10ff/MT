package com.example.mt.model.gi

sealed class GIProjection(
) {
    object WGS84 : GIProjection()
    object WorldMercator : GIProjection()

    companion object {
        fun reproject(point: GILonLat, source: GIProjection, destination: GIProjection): GILonLat =
            when {
                //todo
                source == WorldMercator && destination == WGS84 -> point
                source == WGS84 && destination == WorldMercator -> point
                else -> point
            }
    }
}
