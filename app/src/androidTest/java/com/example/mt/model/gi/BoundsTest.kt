package com.example.mt.model.gi

import com.example.mt.map.MapUtils
import org.junit.Assert.*
import org.junit.Test

class BoundsTest{
    @Test
    fun scale2z() {

        val bounds = Bounds(Projection.WGS84, -180.0, 90.0, 180.0, -89.0)
        val merc = bounds.reproject(Projection.WorldMercator)
        val wgs = merc.reproject(Projection.WGS84)

        assert(merc != null)
    }
}

