package com.example.mt.map.layer

import android.graphics.Bitmap
import android.graphics.Rect
import com.example.mt.map.renderer.GIXMLRenderer
import com.example.mt.map.wkt.WktConverter
import com.example.mt.map.wkt.WktGeometry
import com.example.mt.map.wkt.WktLayer
import com.example.mt.model.gi.Bounds
import com.example.mt.model.gi.Projection
import com.example.mt.model.gi.VectorStyle
import com.example.mt.model.xml.EditableType
import com.example.mt.model.xml.GILayerType
import com.example.mt.model.xml.SourceLocation
import org.simpleframework.xml.Serializer
import org.simpleframework.xml.convert.Registry
import org.simpleframework.xml.convert.RegistryStrategy
import org.simpleframework.xml.core.Persister
import java.io.File

class XMLLayer(
    name: String?,
    type: GILayerType,
    enabled: Boolean,
    source: String,
    sourceLocation: SourceLocation,
    rangeFrom: Int?,
    rangeTo: Int?,
    val style: VectorStyle,
    val editableType: EditableType?,
    val activeEdiable: Boolean?
) : Layer(
    name,
    type,
    enabled,
    source,
    sourceLocation,
    rangeFrom,
    rangeTo,
    Projection.WGS84,
    GIXMLRenderer()
) {
    val geometries =
        try {
            serializer.read(WktLayer::class.java, File(source)).geometry
        } catch (e: Exception) {
            mutableListOf<WktGeometry>()
        }

    override suspend fun renderBitmap(
        area: Bounds,
        rect: Rect,
        opacity: Int,
        scale: Double
    ): Bitmap? {
        return renderer.renderBitmap(this, area, opacity, rect, scale)
    }

    companion object {
        val registry = Registry()
        val strategy = RegistryStrategy(registry)
        val serializer: Serializer = Persister(strategy)
        val rr = registry.bind(WktLayer::class.java, WktConverter::class.java)
    }
}