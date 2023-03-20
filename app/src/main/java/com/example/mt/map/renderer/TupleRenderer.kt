package com.example.mt.map.renderer

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Paint.Style
import android.graphics.Rect
import com.example.mt.map.Screen
import com.example.mt.map.layer.Layer
import com.example.mt.map.layer.TupleLayer
import com.example.mt.map.tile.GITile
import com.example.mt.model.gi.Bounds
import kotlin.math.ln
import kotlin.math.roundToInt

object TupleRenderer : GIRenderer() {

    val paint = Paint()
        .apply {
            this.color = Color.MAGENTA
            style = Style.STROKE
            strokeWidth = 1f

        }
    val text = Paint().apply {
        this.color = Color.MAGENTA
        textSize = 32f
        style = Style.FILL
    }

    override suspend fun renderBitmap(
        canvas: Canvas,
        layer: Layer,
        area: Bounds,
        opacity: Int,
        rect: Rect,
        scale: Float
    ) {
        (layer as? TupleLayer)
            ?.let { _ ->
                val bounds = area.reproject(layer.projection)
                val widthPx = rect.width()
                val kf: Double = 360.0 / 256
                val dz = ln(widthPx * kf / bounds.width) / ln(2.0)

                val zoom = dz.roundToInt()
                val screen = Screen(rect, bounds)

                val leftTop =
                    GITile.create(zoom, bounds.left, bounds.top, layer.sqlProjection)
                val rightBottom =
                    GITile.create(
                        zoom,
                        bounds.right,
                        bounds.bottom,
                        layer.sqlProjection
                    )
                for (x in leftTop.x..rightBottom.x)
                    for (y in leftTop.y..rightBottom.y) {
                        val tile = GITile.create(
                            x,
                            y,
                            zoom,
                            layer.sqlProjection
                        )
                        val dst = screen.toScreen(tile.bounds)
                        canvas.drawRect(dst, paint)
                        canvas.drawText("Z=$zoom X=$x Y=$y", dst.left, dst.bottom, text)
                    }

            }
    }
}