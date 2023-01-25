package com.example.mt.map.renderer

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Rect
import com.example.mt.map.Screen
import com.example.mt.map.layer.Layer
import com.example.mt.map.layer.SQLLayer
import com.example.mt.map.tile.GISQLYandexTile
import com.example.mt.map.tile.GITile
import com.example.mt.model.gi.Bounds
import kotlin.math.ln
import kotlin.math.roundToInt

class GISQLRenderer : GIRenderer() {
    override suspend fun renderBitmap(
        layer: Layer,
        area: Bounds,
        opacity: Int,
        rect: Rect,
        scale: Float
    ): Bitmap? {
        return (layer as? SQLLayer)
            ?.let { sqlLayer ->

                sqlLayer.sqldb.let { properties ->
                    val bounds = area.reproject(layer.projection)
                    val widthPx = rect.width()
                    val kf: Double = 360.0 / (GITile.tilePx * properties.ratio)
                    val dz = ln(widthPx * kf / bounds.width) / ln(2.0)

                    val zoom = dz.roundToInt()
                    val z: Int = properties.getLevel(zoom)
                    val screen = Screen(rect, bounds)
                    if ((properties.minZ < zoom || properties.maxZ > z))
                        try {

                            val bitmap = Bitmap.createBitmap(
                                rect.width(),
                                rect.height(),
                                Bitmap.Config.ARGB_8888
                            )
                            val canvas = Canvas(bitmap)

                            val leftTop =
                                GITile.create(z, bounds.left, bounds.top, layer.type)
                            val rightBottom =
                                GITile.create(
                                    z,
                                    bounds.right,
                                    bounds.bottom,
                                    layer.type
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
                                        //todo
                                        // GITile.create(z, bounds.left, bounds.top, layer.type)
                                        val tile = GISQLYandexTile(x, y, z)
                                        //
                                        val src = Rect(0, 0, bitTile.width, bitTile.width)
                                        val dst = screen.toScreen(tile.bounds)
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