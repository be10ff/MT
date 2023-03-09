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
        area: Bounds,
        rect: Rect,
        opacity: Int,
        scale: Float
    ): Bitmap?

//    abstract fun copy_deep (update: (Layer) -> Layer ) : Lay

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
                rangeFrom = null,
                rangeTo = null,
                editableType = EditableType.POI,
                activeEdiable = true,
                style = VectorStyle.default
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
                rangeFrom = null,
                rangeTo = null,
                editableType = EditableType.TRACK,
                activeEdiable = true,
                style = VectorStyle.default
            )
        }

        fun addSQLiteLayer(fileName: String): SQLLayer {
            return SQLLayer(
                name = fileName,
                type = GILayerType.SQL,
                enabled = true,
                source = fileName,
                rangeFrom = null,
                rangeTo = null,
                sqlProjection = SqlProjection.GOOGLE,
                sqldb = GISQLDB()

            )
        }

        fun addXmlLayer(fileName: String): XMLLayer {
            return XMLLayer(
                name = fileName,
                type = GILayerType.XML,
                enabled = true,
                source = fileName,
                rangeFrom = null,
                rangeTo = null,
                editableType = EditableType.TRACK,
                activeEdiable = true,
                style = VectorStyle.default
            )
        }

        fun addLayer(fileName: String): Layer? {
            return when (File(fileName).extension) {
                "sqlitedb" -> GILayerType.SQL
                "xml" -> GILayerType.XML
                else -> null
            }?.let { type ->
                addLayer(type, fileName)
            }
        }

        fun addLayer(type: GILayerType, fileName: String): Layer? {
            return when (type) {
                GILayerType.SQL -> addSQLiteLayer(fileName)
                GILayerType.XML -> addXmlLayer(fileName)
                else -> null
            }
        }

    }
}
