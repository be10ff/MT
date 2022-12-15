package com.example.mt.map.layer

import android.graphics.*
import com.example.mt.model.gi.Bounds
import com.example.mt.model.gi.GITile
import com.example.mt.model.gi.Layer
import com.example.mt.model.xml.GILayerType
import kotlin.math.ln
import kotlin.math.roundToInt

sealed class GIRenderer {
    abstract suspend fun renderBitmap(
        layer: Layer,
        area: Bounds,
        opacity: Int,
        rect: Rect,
        scale: Double
    ): Bitmap?

    class GISQLRenderer : GIRenderer() {
        override suspend fun renderBitmap(
            layer: Layer,
            area: Bounds,
            opacity: Int,
            rect: Rect,
            scale: Double
        ): Bitmap? {
            return (layer as? Layer.SQLLayer)
                ?.let { sqlLayer ->

                    sqlLayer.sqldb.let { properties ->
                        val bounds = area.reproject(layer.projection)
                        val widthPx = rect.width()
                        val kf: Double = 360.0 / (GITile.tilePx * properties.ratio)
                        val dz = ln(widthPx * kf / bounds.width) / ln(2.0)

                        val zoom = dz.roundToInt()
                        val z: Int = properties.getLevel(zoom)

                        val koeffX: Float = (rect.width() / (bounds.right - bounds.left)).toFloat()
                        val koeffY: Float = (rect.height() / (bounds.top - bounds.bottom)).toFloat()
                        if ((properties.minZ < zoom || properties.maxZ > z))
                            try {

                                val bitmap = Bitmap.createBitmap(
                                    rect.width(),
                                    rect.height(),
                                    Bitmap.Config.ARGB_8888
                                )
                                val canvas = Canvas(bitmap)

                                val leftTop =
                                    GITile.create(z, bounds.left, bounds.top, GILayerType.SQL_LAYER)
                                val rightBottom =
                                    GITile.create(
                                        z,
                                        bounds.right,
                                        bounds.bottom,
                                        GILayerType.SQL_LAYER
                                    )

                                val sqlString = String.format(
                                    "SELECT image, x, y FROM tiles WHERE (x >= %d AND x <= %d) AND (y >= %d AND y <= %d) AND z = %d",
                                    leftTop.x,
                                    rightBottom.x,
                                    leftTop.y,
                                    rightBottom.y,
                                    17 - z
                                )
                                sqlLayer.db?.rawQuery(sqlString, null)
                                    ?.let { cursor ->
                                        while (cursor.moveToNext()) {
                                            val blob = cursor.getBlob(0)
                                            val bitTile =
                                                BitmapFactory.decodeByteArray(
                                                    blob,
                                                    0,
                                                    blob.size
                                                )
                                            val x = cursor.getInt(1)
                                            val y = cursor.getInt(2)
                                            val tile = GITile.GISQLYandexTile(x, y, z)
                                            //
                                            val src = Rect(0, 0, bitTile.width, bitTile.width)
                                            val dst = RectF(
                                                ((tile.bounds.topLeft.lon - bounds.left) * koeffX).toFloat(),
                                                (bitmap.height - (tile.bounds.topLeft.lat - bounds.bottom) * koeffY).toFloat(),
                                                ((tile.bounds.bottomRight.lon - bounds.left) * koeffX).toFloat(),
                                                (bitmap.height - (tile.bounds.bottomRight.lat - bounds.bottom) * koeffY).toFloat()
                                            )
                                            canvas.drawBitmap(bitTile, src, dst, null)
                                        }
                                        cursor.close()
                                    }
                                bitmap

                            } catch (e: Exception) {
                                e.printStackTrace()
                                null
                            }
                        else null
                    }
                }
        }
    }

    class GIXMLRenderer : GIRenderer() {
        override suspend fun renderBitmap(
            layer: Layer,
            area: Bounds,
            opacity: Int,
            rect: Rect,
            scale: Double
        ): Bitmap? {
            return null
        }
    }

    class GIOnlineRenderer : GIRenderer() {
        override suspend fun renderBitmap(
            layer: Layer,
            area: Bounds,
            opacity: Int,
            rect: Rect,
            scale: Double
        ): Bitmap? {
            return null
        }
    }
}