package com.example.mt.map.renderer

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import com.example.mt.map.layer.Layer
import com.example.mt.map.tile.GIYandexTrafficTile
import com.example.mt.model.gi.Bounds

class GIOnlineRenderer : GIRenderer() {
    private val cache = emptyList<GIYandexTrafficTile>()
    override suspend fun renderBitmap(
        layer: Layer,
        area: Bounds,
        opacity: Int,
        rect: Rect,
        scale: Double
    ): Bitmap? {
        val bitmap = Bitmap.createBitmap(
            rect.width(),
            rect.height(),
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        val bounds = area.reproject(layer.projection)
        return null
    }
}