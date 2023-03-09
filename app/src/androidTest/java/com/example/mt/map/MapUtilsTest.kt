package com.example.mt.map

import org.junit.Test

class MapUtilsTest {
    val con = 0.0254 * 0.0066 * 256 / (0.5 * 40000000)

    @Test
    fun scale2z() {

        val res = MapUtils.scale2z(455106)
        res.toString()
        assert(res == 10)
    }

    @Test
    fun z2scale() {
        val res = MapUtils.z2scale(10)
        res.toString()
        assert(res == 455106)
    }
}