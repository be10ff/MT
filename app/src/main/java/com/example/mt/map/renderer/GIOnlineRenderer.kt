package com.example.mt.map.renderer

import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Rect
import com.example.mt.map.layer.Layer
import com.example.mt.map.tile.GISQLYandexTile
import com.example.mt.map.tile.GITile
import com.example.mt.map.tile.GIYandexTrafficTile
import com.example.mt.model.gi.Bounds
import com.example.mt.model.gi.Project
import com.example.mt.model.xml.SqlProjection
import java.net.HttpURLConnection
import java.net.URL
import kotlin.math.ln
import kotlin.math.roundToInt

object GIOnlineRenderer : GIRenderer() {
    private val cache = emptyList<GIYandexTrafficTile>()
    override suspend fun renderBitmap(
        canvas: Canvas,
        layer: Layer,
        area: Bounds,
        opacity: Int,
        rect: Rect,
        scale: Float
    ) {

        val bounds = area.reproject(layer.projection)
        val widthPx = rect.width()
        val kf: Double = 360.0 / (GITile.tilePx)
        val dz = ln(widthPx * kf / bounds.width) / ln(2.0)
        val z = dz.roundToInt()
        val leftTop =
            GITile.create(z, bounds.left, bounds.top, SqlProjection.YANDEX)
        val rightBottom =
            GITile.create(
                z,
                bounds.right,
                bounds.bottom,
                SqlProjection.YANDEX
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
                        val dst = Project.toScreen(rect, bounds,tile.bounds)
                        canvas.drawBitmap(bitTile, src, dst, null)
                    }
                } catch (e: Exception) {
                }

            }
        }
    }
}