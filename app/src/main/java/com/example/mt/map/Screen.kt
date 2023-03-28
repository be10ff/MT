package com.example.mt.map

import android.graphics.Canvas
import android.graphics.Point
import android.graphics.PointF
import android.graphics.Rect
import android.graphics.RectF
import com.example.mt.model.gi.Bounds
import com.example.mt.model.gi.GILonLat
import com.example.mt.model.gi.Projection

class Screen(val rect: Rect, val bounds: Bounds) {
    constructor(canvas: Canvas, bounds: Bounds) : this(
        Rect(0, 0, canvas.width, canvas.height),
        bounds
    )


    val koeffX: Float = (rect.width() / (bounds.right - bounds.left)).toFloat()
    val koeffY: Float = (rect.height() / (bounds.top - bounds.bottom)).toFloat()
    val pixelWeight = (bounds.right - bounds.left) / rect.width().toFloat()

    fun toScreen(point: GILonLat): PointF {

        val mercatorBounds = bounds.reproject(Projection.WorldMercator)
        val mercatorKoeffX: Float =
            (rect.width() / (mercatorBounds.right - mercatorBounds.left)).toFloat()
        val mercatorKoeffY: Float =
            (rect.height() / (mercatorBounds.top - mercatorBounds.bottom)).toFloat()
        val mapPoint = Projection.reproject(point, Projection.WorldMercator)
        val x = ((mapPoint.lon - mercatorBounds.left) * mercatorKoeffX).toFloat()
        val y = (rect.height() - (mapPoint.lat - mercatorBounds.bottom) * mercatorKoeffY).toFloat()
        return PointF(x, y)
    }

    fun toMercatorScreen(point: GILonLat): PointF {
        val x = ((point.lon - bounds.left) * koeffX).toFloat()
        val y = (rect.height() - (point.lat - bounds.bottom) * koeffY).toFloat()
        return PointF(x, y)
    }

    fun toScreen(bounds: Bounds): RectF =
        (toScreen(bounds.topLeft) to toScreen(bounds.bottomRight)).rectF()

    fun screenToLonLat(point: Point) : GILonLat {
        val pixelWidth = bounds.width / rect.width()
        val pixelHeight = bounds.height /rect.height()
        return GILonLat(
            bounds.left + pixelWidth*point.x,
            bounds.top - pixelHeight*point.y,
            bounds.projection
        )
    }

    fun touchArea(point: Point, slop: Int) : Bounds {
        val pixelWidth = bounds.width / rect.width()
        val pixelHeight = bounds.height /rect.height()
        return Bounds(
            bounds.projection,
            bounds.left + pixelWidth*(point.x - slop),
            bounds.top - pixelHeight*(point.y - slop),
            bounds.left + pixelWidth*(point.x + slop),
            bounds.top - pixelHeight*(point.y + slop)
        )
    }

}

fun RectF.ofCorners(topLeft: GILonLat, bottomRight: GILonLat): RectF =
    RectF(
        topLeft.lon.toFloat(),
        topLeft.lat.toFloat(),
        bottomRight.lon.toFloat(),
        bottomRight.lat.toFloat()
    )

fun Pair<PointF, PointF>.rectF(): RectF =
    RectF(
        first.x,
        first.y,
        second.x,
        second.y
    )