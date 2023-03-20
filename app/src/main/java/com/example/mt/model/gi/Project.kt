package com.example.mt.model.gi

import android.graphics.PointF
import android.graphics.Rect
import com.example.mt.map.MapUtils
import com.example.mt.map.Screen
import com.example.mt.map.layer.Layer
import kotlin.math.hypot

data class Project(
    val name: String?,
    val saveAs: String,
    val description: String?,
    val bounds: Bounds,
    val layers: List<Layer>,
    val markerFile: String,
    val markerSource: String,
    val screen: Rect
) {
    val pixelWidth get() = bounds.width / screen.width()
    val pixelHeight get() = bounds.height / screen.height()
    val ratio = screen.width().toDouble() / screen.height().toDouble()
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

    fun mapToScreen(lonlat: GILonLat): PointF {
//        val wgsBounds = bounds.reproject(Projection.WGS84)
//        val _pixelWidth = wgsBounds.width / screen.width()
//        val _pixelHeight = wgsBounds.height / screen.height()
//        val res = try {
//            /*val res =*/ Point(
//                ((lonlat.lon - wgsBounds.left) / _pixelWidth).roundToInt(),
//                ((wgsBounds.top - lonlat.lat) / _pixelHeight).roundToInt()
//            )
//        } catch (e: Exception) {
//            Point(0, 0)
//        }
        return Screen(screen, bounds).toScreen(lonlat)
//        return res
    }

    val metersInPixel: Double
        get() {
            val projected = bounds.reproject(Projection.WGS84)
            val distance = MapUtils(projected.topLeft, projected.bottomRight).getDistance()
            val hypot = hypot(screen.width().toDouble(), screen.height().toDouble())
            return distance / hypot
        }

    companion object {
        val InitialState = Project(
            name = "DefaultProject",
            saveAs = "DefaultProject.pro",
            description = "DefaultProject",
            bounds = Bounds.InitialState,
            layers = emptyList(),
            markerFile = "",
            markerSource = "",
            screen = Rect()
        )
    }
}