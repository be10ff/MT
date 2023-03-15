package com.example.mt.map.layer

import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
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
    Projection.WGS84, GISQLRenderer()
) {

//    init {
//        var _db: SQLiteDatabase? = null
//        try {
//
//            _db =
//                SQLiteDatabase.openDatabase(source, null, SQLiteDatabase.OPEN_READONLY).also { db ->
//                    val sqlString = "SELECT min(z), max(z) FROM tiles"
//                    db.rawQuery(sqlString, null)
//                        ?.let { cursor ->
//                            while (cursor.moveToNext()) {
//                                sqldb.maxZ = 17 - cursor.getInt(0)
//                                sqldb.minZ = 17 - cursor.getInt(1)
//                            }
//                            cursor.close()
//                        }
//                }
//        } finally {
//            _db?.close()
//        }
//    }

    fun proceedSql(job: (SQLiteDatabase) -> Unit) {
        var _db: SQLiteDatabase? = null
        try {
            _db =
                SQLiteDatabase.openDatabase(source, null, SQLiteDatabase.OPEN_READONLY)
                    .also { database ->
                        job(database)
                    }
        } finally {
            _db?.close()
        }
    }

//    val db: SQLiteDatabase? =
//        try {
//            val f = File(source)
//            val res = f.canRead()
//            SQLiteDatabase.openDatabase(source, null, SQLiteDatabase.OPEN_READONLY).also { db ->
//                val sqlString = "SELECT min(z), max(z) FROM tiles"
//                db.rawQuery(sqlString, null)
//                    ?.let { cursor ->
//                        while (cursor.moveToNext()) {
//                            sqldb.maxZ = 17 - cursor.getInt(0)
//                            sqldb.minZ = 17 - cursor.getInt(1)
//                        }
//                        cursor.close()
//                    }
//            }
//
//
//        } catch (e: Exception) {
//            null
//        }
//            ?.also { db ->
//                val sqlString = "SELECT min(z), max(z) FROM tiles"
//                db.rawQuery(sqlString, null)
//                    ?.let { cursor ->
//                        while (cursor.moveToNext()) {
//                            sqldb.maxZ = 17 - cursor.getInt(0)
//                            sqldb.minZ = 17 - cursor.getInt(1)
//                        }
//                        cursor.close()
//                    }
//            }


    override suspend fun renderBitmap(
        area: Bounds,
        rect: Rect,
        opacity: Int,
        scale: Float
    ): Bitmap? {
        return renderer.renderBitmap(this, area, opacity, rect, scale)
    }
}