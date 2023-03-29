package com.example.mt.map.layer

import android.graphics.Canvas
import android.graphics.Rect
import android.os.Environment
import com.example.mt.map.MapUtils
import com.example.mt.map.renderer.GIRenderer
import com.example.mt.model.gi.Bounds
import com.example.mt.model.gi.Projection
import com.example.mt.model.gi.VectorStyle
import com.example.mt.model.xml.EditableType
import com.example.mt.model.xml.GILayerType
import com.example.mt.model.xml.GISQLDB
import com.example.mt.model.xml.SqlProjection
import java.io.File

sealed class Layer(
    open val name: String?,
    open val type: GILayerType,
    open val enabled: Boolean,
    open val source: String,
    open val rangeFrom: Int?,
    open val rangeTo: Int?,
    val projection: Projection,
    val renderer: GIRenderer
) {

    abstract suspend fun renderBitmap(
        canvas: Canvas,
        area: Bounds,
        rect: Rect,
        opacity: Int,
        scale: Float
    )

    companion object {
        fun createPoiLayer(project: String, fileName: String): XMLLayer {
            val dir =
                File(Environment.getExternalStorageDirectory().absolutePath + File.separator + project)
            if (!dir.exists()) dir.mkdir()

            return XMLLayer(
                name = fileName,
                type = GILayerType.XML,
                enabled = true,
                source = Environment.getExternalStorageDirectory().absolutePath + File.separator + project + File.separator + fileName,
                rangeFrom = 0,
                rangeTo = 24,
                editableType = EditableType.POI,
                activeEdiable = true,
                style = VectorStyle.default,
                isMarkersSource = true
            )
        }

        fun createTrackLayer(project: String, fileName: String): XMLLayer {
            val dir =
                File(Environment.getExternalStorageDirectory().absolutePath + File.separator + project)
            if (!dir.exists()) dir.mkdir()

            return XMLLayer(
                name = fileName,
                type = GILayerType.XML,
                enabled = true,
                source = Environment.getExternalStorageDirectory().absolutePath + File.separator + project + File.separator + fileName,
                rangeFrom = 0,
                rangeTo = 24,
                editableType = EditableType.TRACK,
                activeEdiable = true,
                style = VectorStyle.default,
                isMarkersSource = false
            )
        }

        private fun addSQLiteLayer(fileName: String): SQLLayer {
            return SQLLayer(
                name = fileName,
                type = GILayerType.SQL,
                enabled = true,
                source = fileName,
                rangeFrom = null,
                rangeTo = null,
                sqlProjection = SqlProjection.GOOGLE,
                sqldb = GISQLDB()
            ).run {
                proceedSql { db ->
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
                copy(
                    rangeTo = MapUtils.z2scale(sqldb.minZ),
                    rangeFrom = MapUtils.z2scale(sqldb.maxZ)
                )
            }

        }

        private fun addXmlLayer(fileName: String): XMLLayer {
            return XMLLayer(
                name = fileName,
                type = GILayerType.XML,
                enabled = true,
                source = fileName,
                rangeFrom = null,
                rangeTo = null,
                editableType = EditableType.TRACK,
                activeEdiable = true,
                style = VectorStyle.default,
                isMarkersSource = false
            )
        }

        private fun addFolderLayer(fileName: String): FolderLayer {
            val folder = File(fileName).parent?.let{
                File(it).parent
            } ?: ""
            return FolderLayer(
                name = folder,
                type = GILayerType.FOLDER,
                enabled = true,
                source = folder,
                rangeFrom = null,
                rangeTo = null,
                sqlProjection = SqlProjection.GOOGLE,
                sqldb = GISQLDB()
            ).run {
                File(folder).list { file, s ->
                    s.startsWith("Z")
                }?.mapNotNull{
                    it.substring(1).toIntOrNull()
                }?.let{
                    sqldb.maxZ = it.max()
                    sqldb.minZ = it.min()
                }
                copy(
                    rangeTo = MapUtils.z2scale(sqldb.minZ),
                    rangeFrom = MapUtils.z2scale(sqldb.maxZ)
                )
            }
        }

        fun addLayer(fileName: String): Layer? {
            return when (File(fileName).extension) {
                "sqlitedb" -> GILayerType.SQL
                "xml" -> GILayerType.XML
                "png" -> GILayerType.FOLDER
                else -> null
            }?.let { type ->
                addLayer(type, fileName)
            }
        }

        private fun addLayer(type: GILayerType, fileName: String): Layer? {
            return when (type) {
                GILayerType.SQL -> addSQLiteLayer(fileName)
                GILayerType.XML -> addXmlLayer(fileName)
                GILayerType.FOLDER -> addFolderLayer(fileName)
                else -> null
            }
        }

    }
}
