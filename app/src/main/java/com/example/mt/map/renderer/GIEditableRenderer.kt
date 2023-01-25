package com.example.mt.map.renderer

import android.graphics.Bitmap
import android.graphics.Rect
import com.example.mt.map.layer.Layer
import com.example.mt.model.gi.Bounds

class GIEditableRenderer : GIRenderer() {
    override suspend fun renderBitmap(
        layer: Layer,
        area: Bounds,
        opacity: Int,
        rect: Rect,
        scale: Float
    ): Bitmap? {
        return null
    }
}