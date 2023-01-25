package com.example.mt.map.layer

import android.graphics.Bitmap
import android.graphics.Rect
import android.os.Environment
import com.example.mt.map.renderer.GIRenderer
import com.example.mt.model.gi.Bounds
import com.example.mt.model.gi.Projection
import com.example.mt.model.gi.VectorStyle
import com.example.mt.model.xml.EditableType
import com.example.mt.model.xml.GILayerType
import com.example.mt.model.xml.SourceLocation
import java.io.File

sealed class Layer(
    val name: String?,
    val type: GILayerType,
    val enabled: Boolean,
    val source: String,
    val sourceLocation: SourceLocation,
    val rangeFrom: Int?,
    val rangeTo: Int?,
    val projection: Projection,
    val renderer: GIRenderer
) {
    abstract suspend fun renderBitmap(
        area: Bounds,
        rect: Rect,
        opacity: Int,
        scale: Float
    ): Bitmap?

    companion object {
        fun createPoiLayer(project: String, fileName: String): XMLLayer {
            val dir =
                File(Environment.getExternalStorageDirectory().absolutePath + File.separator + project)
            if (!dir.exists()) dir.mkdir()
            val layer = XMLLayer(
                name = fileName,
                type = GILayerType.XML,
                enabled = true,
                source = Environment.getExternalStorageDirectory().absolutePath + File.separator + project + File.separator + fileName,
                sourceLocation = SourceLocation.absolute,
                rangeFrom = null,
                rangeTo = null,
                editableType = EditableType.POI,
                activeEdiable = true,
                style = VectorStyle.default
            )

            return layer
        }

        fun createTrackLayer(project: String, fileName: String): XMLLayer {
            val dir =
                File(Environment.getExternalStorageDirectory().absolutePath + File.separator + project)
            if (!dir.exists()) dir.mkdir()
            val layer = XMLLayer(
                name = fileName,
                type = GILayerType.XML,
                enabled = true,
                source = Environment.getExternalStorageDirectory().absolutePath + File.separator + project + File.separator + fileName,
                sourceLocation = SourceLocation.absolute,
                rangeFrom = null,
                rangeTo = null,
                editableType = EditableType.TRACK,
                activeEdiable = true,
                style = VectorStyle.default
            )

            return layer
        }
    }
//    class SQLLayer(
//        name: String?,
//        type: GILayerType,
//        enabled: Boolean,
//        source: String,
//        sourceLocation: SourceLocation,
//        rangeFrom: Int?,
//        rangeTo: Int?,
//        val sqldb: GISQLDB
//    ) : Layer(
//        name, type, enabled, source, sourceLocation, rangeFrom, rangeTo,
//        Projection.WGS84, GIRenderer.GISQLRenderer()
//    ) {
//
//        val db: SQLiteDatabase? =
//            try {
//                val f = File(source)
//                val res = f.canRead()
//                SQLiteDatabase.openDatabase(source, null, SQLiteDatabase.OPEN_READONLY)
//
//
//            } catch (e: Exception) {
//                null
//            }
//                ?.also { db ->
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
//
//        override suspend fun renderBitmap(
//            area: Bounds,
//            rect: Rect,
//            opacity: Int,
//            scale: Double
//        ): Bitmap? {
//            return Mutex().withLock {
//                renderer.renderBitmap(this, area, opacity, rect, scale)
//            }
//        }
//    }

//    class XMLLayer(
//        name: String?,
//        type: GILayerType,
//        enabled: Boolean,
//        source: String,
//        sourceLocation: SourceLocation,
//        rangeFrom: Int?,
//        rangeTo: Int?,
//        val style: GIStyle,
//        val editableType: EditableType?,
//        val activeEdiable: Boolean?
//    ) : Layer(
//        name,
//        type,
//        enabled,
//        source,
//        sourceLocation,
//        rangeFrom,
//        rangeTo,
//        Projection.WGS84,
//        GIRenderer.GIXMLRenderer()
//    ) {
//
//
//        override suspend fun renderBitmap(
//            area: Bounds,
//            rect: Rect,
//            opacity: Int,
//            scale: Double
//        ): Bitmap? {
//            return null
//        }
//    }

//    class TrafficLayer(
//        name: String?,
//        type: GILayerType,
//        enabled: Boolean,
//        source: String,
//        sourceLocation: SourceLocation,
//        rangeFrom: Int?,
//        rangeTo: Int?,
//    ) : Layer(
//        name,
//        type,
//        enabled,
//        source,
//        sourceLocation,
//        rangeFrom,
//        rangeTo,
//        Projection.WGS84,
//        GIRenderer.GIOnlineRenderer()
//    ) {
//        override suspend fun renderBitmap(
//            area: Bounds,
//            rect: Rect,
//            opacity: Int,
//            scale: Double
//        ): Bitmap? {
//            return null
//        }
//    }
}
