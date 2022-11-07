package com.example.mt.map.layer

import android.database.Cursor
import android.graphics.*
import com.example.mt.model.gi.GIBounds
import com.example.mt.model.gi.GITile
import com.example.mt.model.properties.ZoomingType
import kotlin.math.ln
import kotlin.math.roundToInt


sealed class GIRenderer {
    abstract fun renderImage(
        layer: GILayer,
        area: GIBounds,
        opacity: Int,
        bitmap: Bitmap,
        scale: Double
    )

    class GISQLRenderer : GIRenderer() {
        override fun renderImage(
            layer: GILayer,
            area: GIBounds,
            opacity: Int,
            bitmap: Bitmap,
            scale: Double
        ) {
//            (layer as? GILayer.GISQLLayer)
            GILayer.sqlTest
                .let { sqlLayer ->
                    sqlLayer.properties?.sqldbProperties?.let { properties ->
                        val bounds = area.reproject(layer.projection)
                        val widthPx = bitmap.width
                        val kf: Double = 360.0 / (GITile.tilePx * properties.ratio)
                        val dz = ln(widthPx * kf / bounds.width) / ln(2.0)

                        val z = dz.roundToInt()
                        val zoom: Int = properties.getLevel(z)

                        val koeffX: Float = (bitmap.width / (bounds.right - bounds.left)).toFloat()
                        val koeffY: Float = (bitmap.height / (bounds.top - bounds.bottom)).toFloat()

                        when {
                            //todo very strange all Zooming types skipped
                            properties.zoomingType == ZoomingType.AUTO && (properties.minZ > zoom || properties.maxZ < z) -> {}
                            (properties.zoomingType == ZoomingType.SMART || properties.zoomingType == ZoomingType.ADAPTIVE) && (properties.minZ > zoom || properties.maxZ < z) -> {}
                            else -> try {

                                val canvas = Canvas(bitmap)

                                sqlLayer.getTiles(bounds, z)
                                    .forEach { tile ->
                                        val sqlString = String.format(
                                            "SELECT image FROM tiles WHERE x=%d AND y=%d AND z=%d",
                                            tile.x,
                                            tile.y,
                                            17 - tile.z
                                        )

                                        val cursor: Cursor = sqlLayer.db.rawQuery(sqlString, null)
                                        (if (cursor.moveToFirst()) {
                                            var bitTile: Bitmap? = null
                                            while (!cursor.isAfterLast) {
                                                val blob = cursor.getBlob(0)
                                                bitTile =
                                                    BitmapFactory.decodeByteArray(
                                                        blob,
                                                        0,
                                                        blob.size
                                                    )
                                                cursor.moveToNext()
                                            }

                                            bitTile
                                        } else null)
                                            .also { cursor.close() }
                                            ?.let { bit ->

                                                val src = Rect(0, 0, bit.width, bit.width)
                                                val dst = RectF(
                                                    ((tile.bounds.topLeft.lon - bounds.left) * koeffX).toFloat(),
                                                    (bitmap.height - (tile.bounds.topLeft.lat - bounds.bottom) * koeffY).toFloat(),
                                                    ((tile.bounds.bottomRight.lon - bounds.left) * koeffX).toFloat(),
                                                    (bitmap.height - (tile.bounds.bottomRight.lat - bounds.bottom) * koeffY).toFloat()
                                                )

                                                canvas.drawBitmap(bit, src, dst, null)
                                            }

                                    }

                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                }
        }
    }
}