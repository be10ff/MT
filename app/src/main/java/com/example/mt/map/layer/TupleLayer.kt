package com.example.mt.map.layer

import android.graphics.Canvas
import android.graphics.Rect
import com.example.mt.map.renderer.TupleRenderer
import com.example.mt.model.gi.Bounds
import com.example.mt.model.gi.Projection
import com.example.mt.model.xml.GILayerType
import com.example.mt.model.xml.SqlProjection

object TupleLayer : Layer(
    "name", GILayerType.SQL, true, "source", 1, 24,
    Projection.WGS84, TupleRenderer
) {
    val sqlProjection = SqlProjection.YANDEX
    override suspend fun renderBitmap(
        canvas: Canvas,
        area: Bounds,
        rect: Rect,
        opacity: Int,
        scale: Float
    ) {
        renderer.renderBitmap(canvas, this, area, opacity, rect, scale)
    }
}