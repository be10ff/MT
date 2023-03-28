package com.example.mt.map.wkt

import android.graphics.Canvas
import com.example.mt.model.gi.Bounds
import com.example.mt.model.gi.VectorStyle


interface WktGeometry {
    val type: WKTGeometryType
    val attributes: MutableMap<String, DBaseField>
    var selected: Boolean
    var marker: Boolean

    fun toWKT(): String
    fun draw(canvas: Canvas, bounds: Bounds, scale: Float, style: VectorStyle)
    fun paint(canvas: Canvas, bounds: Bounds, style: VectorStyle)
    fun isEmpty(): Boolean
    fun delete()
    fun serializedGeometry(): String
    fun serialize(): String
    fun isTouch(bounds: Bounds): Boolean

}
