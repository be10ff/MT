package com.example.mt.map.layer

import android.graphics.Canvas
import android.graphics.Rect
import com.example.mt.map.renderer.FolderRenderer
import com.example.mt.map.tile.GITile
import com.example.mt.model.gi.Bounds
import com.example.mt.model.gi.Projection
import com.example.mt.model.xml.GILayerType
import com.example.mt.model.xml.GISQLDB
import com.example.mt.model.xml.SqlProjection

data class FolderLayer(
    override val name: String?,
    override val type: GILayerType,
    override val enabled: Boolean,
    override val source: String,
    override val rangeFrom: Int?,
    override val rangeTo: Int?,
    val sqlProjection: SqlProjection,
    val sqldb: GISQLDB
) : Layer(
    name, type, enabled, source, rangeFrom, rangeTo,
    Projection.WGS84, FolderRenderer
) {

    fun getPath(tile: GITile) = source+tile.run{"/Z$z/${y}_$x.png"}

    fun getTiles(bounds: Bounds, z: Int): List<GITile> {
        val leftTop =
            GITile.create(z, bounds.left, bounds.top, sqlProjection)
        val rightBottom =
            GITile.create(
                z,
                bounds.right,
                bounds.bottom,
                sqlProjection
            )
        return buildList {
            for(x in leftTop.x..rightBottom.x)
                for(y in leftTop.y..rightBottom.y)
                    add(GITile.create(x, y, z, sqlProjection))
        }
    }

    override suspend fun renderBitmap(
        canvas: Canvas,
        area: Bounds,
        rect: Rect,
        opacity: Int,
        scale: Float
    ) {
        renderer.renderBitmap(canvas, this, area, opacity, rect, scale)
    }
}