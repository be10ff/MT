package com.example.mt.map.renderer

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Rect
import com.example.mt.map.Screen
import com.example.mt.map.layer.Layer
import com.example.mt.map.tile.GISQLYandexTile
import com.example.mt.map.tile.GITile
import com.example.mt.map.tile.GIYandexTrafficTile
import com.example.mt.model.gi.Bounds
import java.net.HttpURLConnection
import java.net.URL
import kotlin.math.ln
import kotlin.math.roundToInt

class GIOnlineRenderer : GIRenderer() {
    private val cache = emptyList<GIYandexTrafficTile>()
    override suspend fun renderBitmap(
        layer: Layer,
        area: Bounds,
        opacity: Int,
        rect: Rect,
        scale: Float
    ): Bitmap? {
        val bitmap = Bitmap.createBitmap(
            rect.width(),
            rect.height(),
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        val bounds = area.reproject(layer.projection)
        val widthPx = rect.width()
        val kf: Double = 360.0 / (GITile.tilePx)
        val dz = ln(widthPx * kf / bounds.width) / ln(2.0)
        val z = dz.roundToInt()
        val screen = Screen(rect, bounds)
        val leftTop =
            GITile.create(z, bounds.left, bounds.top, layer.type)
        val rightBottom =
            GITile.create(
                z,
                bounds.right,
                bounds.bottom,
                layer.type
            )

        for (x in leftTop.x..rightBottom.x) {
            for (y in leftTop.y..rightBottom.y) {
                try {

                    val tile = GISQLYandexTile(x, y, z)
                    val url = URL(tile.url())
                    val connection = url.openConnection() as HttpURLConnection
                    connection.connect()
                    val input = connection.inputStream
                    val bitTile = BitmapFactory.decodeStream(input)
                    connection.disconnect()
                    bitTile?.let {
                        val src = Rect(0, 0, bitTile.width, bitTile.width)
                        val dst = screen.toScreen(tile.bounds)
                        canvas.drawBitmap(bitTile, src, dst, null)
                    }
                } catch (e: Exception) {
                }

            }
        }
        return bitmap
    }
}