package com.example.mt.map.wkt

import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.RectF
import com.example.mt.model.gi.*

data class WktPoint(
    var point: GILonLat,
    override val attributes: MutableMap<String, DBaseField> = mutableMapOf(),
    override var selected: Boolean = false,
    override var marker: Boolean = false
) : WktGeometry {
    override val type: WKTGeometryType = WKTGeometryType.POINT
    val inMap = Projection.reproject(point, Projection.WorldMercator)
    override fun toWKT(): String = "POINT(${point.lon} ${point.lat})"

    override fun draw(canvas: Canvas, bounds: Bounds, scale: Float, style: VectorStyle) {
        val screenPoint = Project.toScreen(canvas, bounds, point)
        val src = Rect(0, 0, style.image.width, style.image.height)
        val dst = RectF(
            screenPoint.x - scale * style.image.width / 2,
            screenPoint.y - scale * style.image.height / 2,
            screenPoint.x + scale * style.image.width / 2,
            screenPoint.y + scale * style.image.height / 2
        )

        canvas.drawBitmap(
            if(selected) style.imageSelected else if(marker) style.marker else style.image,
            src,
            dst,
            null)
    }

    override fun paint(canvas: Canvas, bounds: Bounds, style: VectorStyle) {}

    fun paintTrack(canvas: Canvas, bounds: Bounds, style: VectorStyle) {
        val screenPoint = Project.toScreen(canvas, bounds, inMap)
        .apply {
            x -= style.image.width / 2
            y -= style.image.height / 2
        }
        canvas.drawBitmap(style.image, screenPoint.x, screenPoint.y, null)
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
            WktPoint(GILonLat(it[0], it[1], Projection.WGS84))
        }


