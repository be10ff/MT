package com.example.mt.map.layer

import android.graphics.Canvas
import android.graphics.Rect
import com.example.mt.map.renderer.GIXMLRenderer
import com.example.mt.map.wkt.WktConverter
import com.example.mt.map.wkt.WktGeometry
import com.example.mt.map.wkt.WktLayer
import com.example.mt.model.gi.Bounds
import com.example.mt.model.gi.Projection
import com.example.mt.model.gi.VectorStyle
import com.example.mt.model.mapper.LayerMapper
import com.example.mt.model.xml.EditableType
import com.example.mt.model.xml.GILayerType
import org.simpleframework.xml.Serializer
import org.simpleframework.xml.convert.Registry
import org.simpleframework.xml.convert.RegistryStrategy
import org.simpleframework.xml.core.Persister
import java.io.File

data class XMLLayer(
    override val name: String?,
    override val type: GILayerType,
    override val enabled: Boolean,
    override val source: String,
    override val rangeFrom: Int?,
    override val rangeTo: Int?,
    val style: VectorStyle,
    val isMarkersSource: Boolean,
    val editableType: EditableType?,
    val activeEdiable: Boolean
) : Layer(
    name,
    type,
    enabled,
    source,
    rangeFrom,
    rangeTo,
    Projection.WGS84,
    GIXMLRenderer
) {
    val geometries =
        try {
            serializer.read(WktLayer::class.java, File(source)).geometry.toMutableList()
        } catch (e: Exception) {
            mutableListOf<WktGeometry>()
        }

    val mapper = LayerMapper()
    override suspend fun renderBitmap(
        canvas: Canvas,
        area: Bounds,
        rect: Rect,
        opacity: Int,
        scale: Float
    ) {
        renderer.renderBitmap(canvas, this, area, opacity, rect, scale)
    }

    fun save() {
        val output = File(source)
        if (!output.exists()) output.createNewFile()
        val xmlLayer = mapper.mapFrom(this)
        serializer.write(xmlLayer, output)
    }

    companion object {
        val registry = Registry()
        val strategy = RegistryStrategy(registry)
        val serializer: Serializer = Persister(strategy)
        val rr = registry.bind(WktLayer::class.java, WktConverter::class.java)
    }
}