package com.example.mt.map.renderer

import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Rect
import com.example.mt.map.MapUtils
import com.example.mt.map.layer.FolderLayer
import com.example.mt.map.layer.Layer
import com.example.mt.map.tile.GITile
import com.example.mt.model.gi.Bounds
import com.example.mt.model.gi.Project
import java.io.File
import kotlin.math.ln
import kotlin.math.roundToInt

object FolderRenderer : GIRenderer() {
    private val src = Rect(0, 0, GITile.tilePx, GITile.tilePx)
    override suspend fun renderBitmap(
        canvas: Canvas,
        layer: Layer,
        area: Bounds,
        opacity: Int,
        rect: Rect,
        scale: Float
    ) {
        (layer as? FolderLayer)
            ?.let { folderLayer ->
                folderLayer.sqldb.let { properties ->
                    val bounds = area.reproject(layer.projection)
                    val widthPx = rect.width()
                    val kf: Double = 360.0 / (GITile.tilePx * properties.ratio)
                    val dz = ln(widthPx * kf / bounds.width) / ln(2.0)

                    val zoom = dz.roundToInt()
                    val z: Int = properties.getLevel(zoom)
                    val minScale = MapUtils.scale2z(folderLayer.rangeTo ?: 0)
                    val maxScale = MapUtils.scale2z(folderLayer.rangeFrom ?: 0)

                    if ((minScale <= zoom && maxScale >= zoom))
                        try {
                            layer.getTiles(bounds, z)
                                .mapNotNull { tile ->
                                    if(File(layer.getPath(tile)).exists()){
                                        tile to BitmapFactory.decodeFile(layer.getPath(tile))
                                    } else null
                                }
                                .forEach {(tile, bitmap) ->
                                    src.set(0, 0, bitmap.width, bitmap.width)
                                    val dst = Project.toScreen(rect, bounds, tile.bounds)
                                    canvas.drawBitmap(bitmap, src, dst, null)
                                }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                }
            }
    }
}