package com.example.mt.map.layer

import android.graphics.Bitmap
import android.graphics.Rect
import com.example.mt.map.renderer.GIOnlineRenderer
import com.example.mt.model.gi.Bounds
import com.example.mt.model.gi.Projection
import com.example.mt.model.xml.GILayerType
import com.example.mt.model.xml.SourceLocation

//todo
@Deprecated("todo")
class TrafficLayer(
    name: String?,
    type: GILayerType,
    enabled: Boolean,
    source: String,
    sourceLocation: SourceLocation,
    rangeFrom: Int?,
    rangeTo: Int?,
) : Layer(
    name,
    type,
    enabled,
    source,
    sourceLocation,
    rangeFrom,
    rangeTo,
    Projection.WGS84,
    GIOnlineRenderer()
) {

    //    val url = "http://vec01.maps.yandex.net/tiles"
    override suspend fun renderBitmap(
        area: Bounds,
        rect: Rect,
        opacity: Int,
        scale: Float
    ): Bitmap? {
//        return renderer.renderBitmap(this, area, opacity, rect, scale)
        return null
    }
}