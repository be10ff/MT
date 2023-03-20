package com.example.mt.map.renderer

import android.graphics.Canvas
import android.graphics.Rect
import com.example.mt.map.layer.Layer
import com.example.mt.model.gi.Bounds

sealed class GIRenderer {
    abstract suspend fun renderBitmap(
        canvas: Canvas,
        layer: Layer,
        area: Bounds,
        opacity: Int,
        rect: Rect,
        scale: Float
    )


}