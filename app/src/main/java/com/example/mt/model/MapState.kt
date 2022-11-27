package com.example.mt.model

import android.graphics.Rect
import com.example.mt.model.gi.GIBounds

data class MapState(
    val bounds: GIBounds,
    val viewRect: Rect,
//    val position: Location,
    val zoom: Float,
    val update: Boolean
) {
    val pixelWidth = bounds.width / viewRect.width()
    val pixelHeight = bounds.height / viewRect.height()

    companion object {

//    private fun adjustBoundsRatio(bounds: GIBounds, screenRect: Rect): GIBounds {
//        val scrRatio = screenRect.width().toDouble() / screenRect.height()
//        val areaRatio = bounds.width / bounds.height
//        return when {
//            areaRatio > scrRatio -> {
//                val diff = (bounds.width / scrRatio - bounds.height) / 2
//                GIBounds(
//                    bounds.projection,
//                    bounds.left,
//                    bounds.top + diff,
//                    bounds.right,
//                    bounds.bottom - diff
//                )
//            }
//            areaRatio < scrRatio -> {
//                val diff = (bounds.height * scrRatio - bounds.width) / 2
//                GIBounds(
//                    bounds.projection,
//                    bounds.left - diff,
//                    bounds.top,
//                    bounds.right + diff,
//                    bounds.bottom
//                )
//            }
//            else -> {
//                bounds
//            }
//        }
//
//    }
    }
}

