package com.example.mt.map

import android.graphics.Canvas
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
        val mapPoint = Projection.reproject(point, Projection.WGS84, bounds.projection)
        val x = ((mapPoint.lon - bounds.left) * koeffX).toFloat()
        val y = (rect.height() - (mapPoint.lat - bounds.bottom) * koeffY).toFloat()
        return PointF(x, y)
    }

    fun toMercatorScreen(point: GILonLat): PointF {
        val x = ((point.lon - bounds.left) * koeffX).toFloat()
        val y = (rect.height() - (point.lat - bounds.bottom) * koeffY).toFloat()
        return PointF(x, y)
    }

    fun toScreen(bounds: Bounds): RectF =
        (toScreen(bounds.topLeft) to toScreen(bounds.bottomRight)).rectF()

}

fun RectF.ofCorners(topLeft: GILonLat, bottomRight: GILonLat): RectF =
    RectF(
        topLeft.lon.toFloat(),
        topLeft.lat.toFloat(),
        bottomRight.lon.toFloat(),
        bottomRight.lat.toFloat()
    )

//fun Pair<GILonLat, GILonLat>.rectF(): RectF =
//    RectF(
//        first.lon.toFloat(),
//        first.lat.toFloat(),
//        second.lon.toFloat(),
//        second.lat.toFloat()
//    )


fun Pair<PointF, PointF>.rectF(): RectF =
    RectF(
        first.x,
        first.y,
        second.x,
        second.y
    )