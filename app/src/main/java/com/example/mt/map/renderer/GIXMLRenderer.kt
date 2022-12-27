package com.example.mt.map.renderer

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import com.example.mt.map.layer.Layer
import com.example.mt.map.layer.XMLLayer
import com.example.mt.map.wkt.WktGeometry
import com.example.mt.model.gi.Bounds

class GIXMLRenderer : GIRenderer() {
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
        (layer as? XMLLayer)
            ?.let { xmlLayer ->
                xmlLayer.geometries
                    .map { geometry: WktGeometry ->
                        //todo
                        geometry.draw(canvas, area, 1f, xmlLayer.style)
                    }
            }

        return bitmap
    }
}