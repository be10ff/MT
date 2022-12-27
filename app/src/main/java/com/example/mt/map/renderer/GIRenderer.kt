package com.example.mt.map.renderer

import android.graphics.Bitmap
import android.graphics.Rect
import com.example.mt.map.layer.Layer
import com.example.mt.model.gi.Bounds

sealed class GIRenderer {
    abstract suspend fun renderBitmap(
        layer: Layer,
        area: Bounds,
        opacity: Int,
        rect: Rect,
        scale: Double
    ): Bitmap?

}