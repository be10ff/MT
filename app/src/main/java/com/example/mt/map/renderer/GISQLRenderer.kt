package com.example.mt.map.renderer

import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Rect
import com.example.mt.map.MapUtils
import com.example.mt.map.layer.Layer
import com.example.mt.map.layer.SQLLayer
import com.example.mt.map.tile.GITile
import com.example.mt.model.gi.Bounds
import com.example.mt.model.gi.Project
import kotlin.math.ln
import kotlin.math.roundToInt

object GISQLRenderer : GIRenderer() {

    val src = Rect(0, 0, GITile.tilePx, GITile.tilePx)
    override suspend fun renderBitmap(
        canvas: Canvas,
        layer: Layer,
        area: Bounds,
        opacity: Int,
        rect: Rect,
        scale: Float
    ) {
        (layer as? SQLLayer)
            ?.let { sqlLayer ->
                sqlLayer.sqldb.let { properties ->
                    val bounds = area.reproject(layer.projection)
                    val widthPx = rect.width()
                    val kf: Double = 360.0 / (GITile.tilePx * properties.ratio)
                    val dz = ln(widthPx * kf / bounds.width) / ln(2.0)

                    val zoom = dz.roundToInt()
                    val z: Int = properties.getLevel(zoom)
                    val minScale = MapUtils.scale2z(sqlLayer.rangeTo ?: 0)
                    val maxScale = MapUtils.scale2z(sqlLayer.rangeFrom ?: 0)

                    if ((minScale <= zoom && maxScale >= zoom))
                        try {
                            val leftTop =
                                GITile.create(z, bounds.left, bounds.top, layer.sqlProjection)
                            val rightBottom =
                                GITile.create(
                                    z,
                                    bounds.right,
                                    bounds.bottom,
                                    layer.sqlProjection
                                )

                            val sqlString =
                                "SELECT image, x, y FROM tiles WHERE (x >= ${leftTop.x} AND x <= ${rightBottom.x}) AND (y >= ${leftTop.y} AND y <= ${rightBottom.y}) AND z = ${17 - z}"
                            sqlLayer.proceedSql { db ->
                                db.rawQuery(sqlString, null)
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
                                            val tile = GITile.create(x, y, z, layer.sqlProjection)
                                            src.set(0, 0, bitTile.width, bitTile.width)
                                            val dst = Project.toScreen(rect, bounds,tile.bounds)
                                            canvas.drawBitmap(bitTile, src, dst, null)
                                        }
                                        cursor.close()
                                    }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                }
            }
    }
}