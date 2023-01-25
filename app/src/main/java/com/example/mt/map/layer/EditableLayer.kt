package com.example.mt.map.layer

import android.graphics.Bitmap
import android.graphics.Rect
import com.example.mt.map.renderer.GIEditableRenderer
import com.example.mt.map.wkt.WktGeometry
import com.example.mt.model.gi.Bounds
import com.example.mt.model.gi.Projection
import com.example.mt.model.xml.GILayerType
import com.example.mt.model.xml.SourceLocation

//todo
@Deprecated("todo")
class EditableLayer(
    name: String?,
    type: GILayerType,
    enabled: Boolean,
    source: String,
    sourceLocation: SourceLocation,
    rangeFrom: Int?,
    rangeTo: Int?
) : Layer(
    name,
    type,
    enabled,
    source,
    sourceLocation,
    rangeFrom,
    rangeTo,
    Projection.WGS84,
    GIEditableRenderer()
) {

    val shapes = mutableListOf<WktGeometry>()
//    val id: Long = 0
//    val attributes = emptyMap<String, DBaseField>()

    override suspend fun renderBitmap(
        area: Bounds,
        rect: Rect,
        opacity: Int,
        scale: Float
    ): Bitmap? {
        TODO("Not yet implemented")
    }
}