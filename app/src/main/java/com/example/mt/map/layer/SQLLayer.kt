package com.example.mt.map.layer

import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.Rect
import com.example.mt.map.renderer.GISQLRenderer
import com.example.mt.model.gi.Bounds
import com.example.mt.model.gi.Projection
import com.example.mt.model.xml.GILayerType
import com.example.mt.model.xml.GISQLDB
import com.example.mt.model.xml.SourceLocation
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.File

class SQLLayer(
    name: String?,
    type: GILayerType,
    enabled: Boolean,
    source: String,
    sourceLocation: SourceLocation,
    rangeFrom: Int?,
    rangeTo: Int?,
    val sqldb: GISQLDB
) : Layer(
    name, type, enabled, source, sourceLocation, rangeFrom, rangeTo,
    Projection.WGS84, GISQLRenderer()
) {

    val db: SQLiteDatabase? =
        try {
            val f = File(source)
            val res = f.canRead()
            SQLiteDatabase.openDatabase(source, null, SQLiteDatabase.OPEN_READONLY)


        } catch (e: Exception) {
            null
        }
            ?.also { db ->
                val sqlString = "SELECT min(z), max(z) FROM tiles"
                db.rawQuery(sqlString, null)
                    ?.let { cursor ->
                        while (cursor.moveToNext()) {
                            sqldb.maxZ = 17 - cursor.getInt(0)
                            sqldb.minZ = 17 - cursor.getInt(1)
                        }
                        cursor.close()
                    }
            }

    override suspend fun renderBitmap(
        area: Bounds,
        rect: Rect,
        opacity: Int,
        scale: Float
    ): Bitmap? {
        return Mutex().withLock {
            renderer.renderBitmap(this, area, opacity, rect, scale)
        }
    }
}