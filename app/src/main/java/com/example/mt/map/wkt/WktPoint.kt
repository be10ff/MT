package com.example.mt.map.wkt

import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.RectF
import com.example.mt.map.Screen
import com.example.mt.model.gi.Bounds
import com.example.mt.model.gi.GILonLat
import com.example.mt.model.gi.Projection
import com.example.mt.model.gi.VectorStyle

//@Root(name = "POINT")
data class WktPoint(
    val point: GILonLat
) : WktGeometry/*(WKTGeometryType.POINT, WKTGeometryStatus.NEW)*/ {
    override val type: WKTGeometryType = WKTGeometryType.POINT
    override val status: WKTGeometryStatus = WKTGeometryStatus.NEW
    override val attributes: MutableMap<String, DBaseField> = mutableMapOf()
    val lon = point.lon
    val lat = point.lat
    val inMap = Projection.reproject(point, Projection.WGS84, Projection.WorldMercator)
    var trackId = -1

    override fun toWKT(): String = "POINT($lon $lat)"

    override fun draw(canvas: Canvas, bounds: Bounds, scale: Float, style: VectorStyle) {
        val screen = Screen(canvas, bounds)
        val screenPoint = screen.toScreen(point)
        val src = Rect(0, 0, style.image.width, style.image.height)
        val dst = RectF(
            screenPoint.x - scale * style.image.width / 2,
            screenPoint.y - scale * style.image.height / 2,
            screenPoint.x + scale * style.image.width / 2,
            screenPoint.y + scale * style.image.height / 2
        )

        canvas.drawBitmap(style.image, src, dst, style.pen)
        canvas.drawBitmap(style.image, src, dst, style.brush)
    }

    override fun paint(canvas: Canvas, bounds: Bounds, style: VectorStyle) {}

    fun paintTrack(canvas: Canvas, bounds: Bounds, style: VectorStyle) {
        val screen = Screen(canvas, bounds)
        val screeenPoint = screen.toMercatorScreen(inMap).apply {
            x -= style.image.width / 2
            y -= style.image.height / 2
        }
        canvas.drawBitmap(style.image, screeenPoint.x, screeenPoint.y, null)
    }

    override fun isEmpty(): Boolean = false

    override fun delete() {}

    override fun serializedGeometry(): String = toWKT()

    override fun serialize(): String {
        TODO("Not yet implemented")
    }

    override fun isTouch(bounds: Bounds): Boolean = bounds.contains(point)
}

fun String.pointFromWkt(): WktPoint? =
    substring(indexOfFirst { it == '(' } + 1,
        indexOfFirst { it == ')' })
        .split(' ')
        .mapNotNull {
            it.toDoubleOrNull()
        }
        .takeIf {
            it.size == 2
        }
        ?.let {
            WktPoint(GILonLat(it[0], it[1]))
        }


