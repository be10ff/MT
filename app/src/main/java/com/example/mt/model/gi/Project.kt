package com.example.mt.model.gi

import android.graphics.Canvas
import android.graphics.Point
import android.graphics.PointF
import android.graphics.Rect
import android.graphics.RectF
import android.util.Log
import com.example.mt.map.MapUtils
import com.example.mt.map.layer.Layer
import com.example.mt.map.wkt.WktPoint
import kotlin.math.hypot

data class Project(
    val name: String?,
    val saveAs: String,
    val description: String?,
    val bounds: Bounds,
    val layers: List<Layer>,
    val screen: Rect
) {
    val pixelWidth get() = bounds.width / screen.width()
    val pixelHeight get() = bounds.height / screen.height()
    val ratio get() = screen.width().toDouble() / screen.height().toDouble()
    fun adjustBoundsRatio(screenRect: Rect): Bounds {
        return if (screen.width() == 0) {
            val scrRatio = screenRect.width().toDouble() / screenRect.height()
            val areaRatio = bounds.width / bounds.height
            return when {
                areaRatio > scrRatio -> {
                    val diff = (bounds.width / scrRatio - bounds.height) / 2
                    Bounds(
                        bounds.projection,
                        bounds.left,
                        bounds.top + diff,
                        bounds.right,
                        bounds.bottom - diff
                    )
                }
                areaRatio < scrRatio -> {
                    val diff = (bounds.height * scrRatio - bounds.width) / 2
                    Bounds(
                        bounds.projection,
                        bounds.left - diff,
                        bounds.top,
                        bounds.right + diff,
                        bounds.bottom
                    )
                }
                else -> {
                    bounds
                }
            }
        } else {
            val scaleHalf = bounds.width / (2 * screen.width())
            val areaCenter = bounds.center
            val areaTop = areaCenter.lat + scaleHalf * screenRect.height()
            val areaBottom = areaCenter.lat - scaleHalf * screenRect.height()
            val areaLeft = areaCenter.lon - scaleHalf * screenRect.width()
            val areaRight = areaCenter.lon + scaleHalf * screenRect.width()
            Bounds(
                bounds.projection,
                areaLeft,
                areaTop,
                areaRight,
                areaBottom
            )
        }
    }

    private fun adjustBoundsRatio(bounds: Bounds, screenRect: Rect): Bounds {
        val scrRatio = screenRect.width().toDouble() / screenRect.height()
        val areaRatio = bounds.width / bounds.height
        return when {
            areaRatio > scrRatio -> {
                val diff = (bounds.width / scrRatio - bounds.height) / 2
                Bounds(
                    bounds.projection,
                    bounds.left,
                    bounds.top + diff,
                    bounds.right,
                    bounds.bottom - diff
                )
            }
            areaRatio < scrRatio -> {
                val diff = (bounds.height * scrRatio - bounds.width) / 2
                Bounds(
                    bounds.projection,
                    bounds.left - diff,
                    bounds.top,
                    bounds.right + diff,
                    bounds.bottom
                )
            }
            else -> {
                bounds
            }
        }
    }

    val metersInPixel: Double
        get() {
            val projected = bounds.reproject(Projection.WGS84)
            val distance = MapUtils(projected.topLeft, projected.bottomRight).getDistance()
            val hypot = hypot(screen.width().toDouble(), screen.height().toDouble())
            return distance / hypot
        }

    fun toScreen(point: GILonLat): PointF {

        val mercatorBounds = bounds.reproject(Projection.WorldMercator)
        val mercatorKoeffX: Float =
            (screen.width() / (mercatorBounds.right - mercatorBounds.left)).toFloat()
        val mercatorKoeffY: Float =
            (screen.height() / (mercatorBounds.top - mercatorBounds.bottom)).toFloat()
        val mapPoint = Projection.reproject(point, Projection.WorldMercator)
        val x = ((mapPoint.lon - mercatorBounds.left) * mercatorKoeffX).toFloat()
        val y = (screen.height() - (mapPoint.lat - mercatorBounds.bottom) * mercatorKoeffY).toFloat()
        return PointF(x, y)
    }

    fun touchArea(point: Point, slop: Int) : Bounds {
        val pixelWidth = bounds.width / screen.width()
        val pixelHeight = bounds.height /screen.height()
        return Bounds(
            bounds.projection,
            bounds.left + pixelWidth*(point.x - slop),
            bounds.top - pixelHeight*(point.y - slop),
            bounds.left + pixelWidth*(point.x + slop),
            bounds.top - pixelHeight*(point.y + slop)
        )
    }

    companion object {
        val InitialState = Project(
            name = "DefaultProject",
            saveAs = "DefaultProject.pro",
            description = "DefaultProject",
            bounds = Bounds.InitialState,
            layers = emptyList(),
            screen = Rect(),
        )
            .also{
                Log.e("InitialState","Project")
            }

        private fun toScreen(rect: Rect, bounds: Bounds, point: GILonLat): PointF {
            val mercatorBounds = bounds.reproject(Projection.WorldMercator)
            val mercatorKoeffX: Float =
                (rect.width() / (mercatorBounds.right - mercatorBounds.left)).toFloat()
            val mercatorKoeffY: Float =
                (rect.height() / (mercatorBounds.top - mercatorBounds.bottom)).toFloat()
            val mapPoint = Projection.reproject(point, Projection.WorldMercator)
            val x = ((mapPoint.lon - mercatorBounds.left) * mercatorKoeffX).toFloat()
            val y = (rect.height() - (mapPoint.lat - mercatorBounds.bottom) * mercatorKoeffY).toFloat()
            return PointF(x, y)
        }

        fun toScreen(canvas: Canvas, bounds: Bounds, point: GILonLat): PointF {
            return toScreen(Rect(0, 0, canvas.width, canvas.height), bounds, point)
        }

        fun toScreen(rect: Rect, bounds: Bounds, area: Bounds): RectF =
            (toScreen(rect, bounds, area.topLeft) to toScreen(rect, bounds, area.bottomRight))
                .run{
                    RectF(
                        first.x,
                        first.y,
                        second.x,
                        second.y
                    )
                }

    }
}