package com.example.mt.map.tile

import android.graphics.Bitmap

class GIYandexTrafficTile(x: Int, y: Int, z: Int) : GISQLYandexTile(x, y, z) {
    val REUSE = 20
    val timestamp: Long = System.currentTimeMillis() / 1000
    var bitmap: Bitmap? = null
    var usedAtLastTime = REUSE

}