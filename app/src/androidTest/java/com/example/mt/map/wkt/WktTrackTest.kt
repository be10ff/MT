package com.example.mt.map.wkt

import org.junit.Test

class WktTrackTest {

    @Test
    fun getType() {
        val input = "POINT(37.20716582 55.63004402)\n" +
                "POINT(37.20670902 55.62984426)\n" +
                "POINT(37.20623955 55.62963287)\n" +
                "POINT(37.20576964 55.62942815)\n" +
                "POINT(37.20530065 55.62924383)\n" +
                "POINT(37.20480439 55.62904655)\n" +
                "POINT(37.20432231 55.62887181)\n" +
                "POINT(37.2038716 55.62871574)\n" +
                "POINT(37.20333821 55.62862767)\n" +
                "POINT(37.20277942 55.62856498)\n" +
                "POINT(37.20221966 55.62849141)\n" +
                "POINT(37.20163392 55.62841244)\n" +
                "POINT(37.20111528 55.62831294)\n" +
                "POINT(37.20069621 55.6281356)\n" +
                "POINT(37.20049149 55.62790623)"

        input.lines().asSequence()
            .mapNotNull {
                it.pointFromWkt()
            }
            .toList()

    }
}