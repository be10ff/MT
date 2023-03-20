package com.example.mt.map.renderer

import android.graphics.Canvas
import android.graphics.Rect
import com.example.mt.map.layer.Layer
import com.example.mt.map.layer.XMLLayer
import com.example.mt.map.wkt.WktGeometry
import com.example.mt.model.gi.Bounds

object GIXMLRenderer : GIRenderer() {
    override suspend fun renderBitmap(
        canvas: Canvas,
        layer: Layer,
        area: Bounds,
        opacity: Int,
        rect: Rect,
        scale: Float
    ) {
        (layer as? XMLLayer)
            ?.let { xmlLayer ->
                xmlLayer.geometries
                    .map { geometry: WktGeometry ->
                        geometry.draw(canvas, area, scale, xmlLayer.style)
                    }
            }
    }
}