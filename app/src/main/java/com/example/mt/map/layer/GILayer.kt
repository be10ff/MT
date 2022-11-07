package com.example.mt.map.layer

import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import com.example.mt.map.GILayerType
import com.example.mt.model.gi.GIBounds
import com.example.mt.model.gi.GIProjection
import com.example.mt.model.gi.GITile
import com.example.mt.model.properties.LayerProperties
import com.example.mt.model.properties.SQLDBProperties
import com.example.mt.model.properties.ZoomingType

sealed class GILayer(
//    public val type: GILayerType,
//    public val name: String,
    val path: String,
    val projection: GIProjection,
    val renderer: GIRenderer,
    val properties: LayerProperties?
) {

    class GISQLLayer(type: GILayerType, name: String, path: String, properties: LayerProperties?) :
        GILayer(path, GIProjection.WGS84, GIRenderer.GISQLRenderer(), properties) {
        val db: SQLiteDatabase =
            SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY)

        override fun redraw(area: GIBounds, bitmap: Bitmap, opacity: Int, scale: Double) {
            return synchronized(this) {
                renderer.renderImage(this, area, opacity, bitmap, scale)
            }
        }

        fun getTiles(bounds: GIBounds, z: Int): List<GITile> {
//            return when(properties?.sqldbProperties?.zoomingType){
//                ZoomingType.ADAPTIVE -> emptyList()
//                else -> emptyList()
//            }
            val leftTop = GITile.create(z, bounds.left, bounds.top, GILayerType.SQL)
            val rightBottom = GITile.create(z, bounds.right, bounds.bottom, GILayerType.SQL)
            return buildList {
                for (x in leftTop.x..rightBottom.x) {
                    for (y in leftTop.y..rightBottom.y)
                        add(GITile.GISQLYandexTile(x, y, z))
                }
            }
        }
    }

    companion object {
        fun createLayer(type: GILayerType, name: String, path: String): GILayer {
            return when (type) {
                GILayerType.SQL -> GISQLLayer(type, name, path, null)
                else -> sqlTest
            }
        }

        val sqlTest = GISQLLayer(
            GILayerType.SQL,
            "Worlds.sqlitedb",
            "/storage/emulated/0/Worlds.sqlitedb",
            LayerProperties(
                "Worlds.sqlitedb",
                GILayerType.SQL,
                true,
                SQLDBProperties(ZoomingType.AUTO, 19, 1, 1)
            )
        )
    }

    abstract fun redraw(area: GIBounds, bitmap: Bitmap, opacity: Int, scale: Double)
}