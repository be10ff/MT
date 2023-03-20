package com.example.mt.map.layer

import android.database.sqlite.SQLiteDatabase
import android.graphics.Canvas
import android.graphics.Rect
import com.example.mt.map.renderer.GISQLRenderer
import com.example.mt.model.gi.Bounds
import com.example.mt.model.gi.Projection
import com.example.mt.model.xml.GILayerType
import com.example.mt.model.xml.GISQLDB
import com.example.mt.model.xml.SqlProjection

data class SQLLayer(
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
    Projection.WGS84, GISQLRenderer
) {

    fun proceedSql(job: (SQLiteDatabase) -> Unit) {
        SQLiteDatabase.openDatabase(source, null, SQLiteDatabase.OPEN_READONLY).use(job)
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