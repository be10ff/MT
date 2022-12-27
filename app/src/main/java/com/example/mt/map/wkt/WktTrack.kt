package com.example.mt.map.wkt

import android.graphics.Canvas
import android.graphics.Rect
import com.example.mt.map.Screen
import com.example.mt.model.gi.Bounds
import com.example.mt.model.gi.VectorStyle
import java.io.File
import kotlin.streams.asSequence

data class WktTrack(
    val file: String
) : WktGeometry {
    override val type: WKTGeometryType = WKTGeometryType.TRACK
    override val status: WKTGeometryStatus = WKTGeometryStatus.NEW
    override val attributes: MutableMap<String, DBaseField> = mutableMapOf()
    val points = mutableListOf<WktPoint>()

    init {
        File(file).bufferedReader().lines()
            .asSequence()
            .mapNotNull {
                it.pointFromWkt()
            }
            .also {
                points.addAll(it)
            }
    }

    override fun toWKT(): String {
        val file = File(file)
        return file.absolutePath
    }

    override fun draw(canvas: Canvas, bounds: Bounds, scale: Float, style: VectorStyle) {
        var counter = 0
        val rect = Rect(0, 0, canvas.width, canvas.height)
        val screen = Screen(rect, bounds)
        while (counter < points.size - 1) {
            val prev = screen.toScreen(points[counter].point)
            val current = screen.toScreen(points[counter + 1].point)
            canvas.drawLine(prev.x, prev.y, current.x, current.y, style.pen)
            counter++
        }
    }

    override fun paint(canvas: Canvas, bounds: Bounds, style: VectorStyle) {

    }

    override fun isEmpty(): Boolean {
        TODO("Not yet implemented")
    }

    override fun delete() {
        TODO("Not yet implemented")
    }

    override fun serializedGeometry(): String {
        TODO("Not yet implemented")
    }

    override fun serialize(): String {
        TODO("Not yet implemented")
    }

    override fun isTouch(bounds: Bounds): Boolean {
        TODO("Not yet implemented")
    }
}