package com.example.mt.map.wkt

import android.graphics.Canvas
import android.graphics.Rect
import com.example.mt.model.gi.Bounds
import com.example.mt.model.gi.Project
import com.example.mt.model.gi.VectorStyle
import java.io.BufferedWriter
import java.io.File
import kotlin.math.hypot
import kotlin.streams.asSequence

data class WktTrack(
    val file: String,
    override val attributes: MutableMap<String, DBaseField> = mutableMapOf(),
    override var selected: Boolean = false,
    override var marker: Boolean = false
) : WktGeometry {
    override val type: WKTGeometryType = WKTGeometryType.TRACK
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

    private val writer: BufferedWriter by lazy {
        File(file).bufferedWriter()
    }

    override fun toWKT(): String {
        val file = File(file)
        return file.absolutePath
    }

    override fun draw(canvas: Canvas, bounds: Bounds, scale: Float, style: VectorStyle) {
        var counter = 0
        val rect = Rect(0, 0, canvas.width, canvas.height)
        val pixelWeight = (bounds.right - bounds.left) / rect.width().toFloat()
        while (counter < points.size - 1) {
            var currentIndex = counter + 1
            while (currentIndex < points.size - 1) {
                val distance = hypot(
                    points[currentIndex].inMap.lon - points[counter].inMap.lon,
                    points[currentIndex].inMap.lat - points[counter].inMap.lat
                )
                if (distance > 5 * pixelWeight) break
                currentIndex += 1
            }
            if (bounds.contains(points[currentIndex].point) || bounds.contains(points[counter].point)) {
                val prev = Project.toScreen(canvas, bounds, points[counter].point)
                val current = Project.toScreen(canvas, bounds, points[currentIndex].point)
                canvas.drawLine(prev.x, prev.y, current.x, current.y, style.pen)
            }
            counter = currentIndex
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

    fun stop() {
        try {
            writer.flush()
            writer.close()

        } catch (e: Exception) {
        }
    }

    fun append(line: String) {
        try {
            writer.write(line)
            writer.newLine()
            writer.flush()
        } catch (e: Exception) {
        }
    }
}