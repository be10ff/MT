package com.example.mt.model.gi

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect

@Deprecated("unnesessary")
data class GIBitmap(
    val _bounds: Bounds,
    val bitmap: Bitmap,
    val width: Int,
    val height: Int
) {
    constructor(bounds: Bounds, width: Int, height: Int) :
            this(bounds, Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888).apply {
                eraseColor(Color.WHITE)
            }, width, height)

    constructor(bounds: Bounds, bitmap: Bitmap) : this(
        bounds,
        bitmap,
        bitmap.width,
        bitmap.height
    )

    fun draw(canvas: Canvas, bounds: Bounds) {
        if (bitmap.isRecycled) return
//        if (!_bounds.contains(bounds)) return

        val pixelWidth = bounds.width / canvas.width
        val pixelHeight = bounds.height / canvas.height

        val leftTop = _bounds.topLeft
        val rightBottom = _bounds.bottomRight

        val left: Int = ((leftTop.lon - bounds.left) / pixelWidth).toInt()
        val top: Int = ((bounds.top - leftTop.lat) / pixelHeight).toInt()
        val right: Int = ((rightBottom.lon - bounds.left) / pixelWidth).toInt()
        val bottom: Int = ((bounds.top - rightBottom.lat) / pixelHeight).toInt()
        //TODO re-draw only valid intersection for performance
        canvas.drawBitmap(
            bitmap,
            Rect(0, 0, width, height),
            Rect(left, top, right, bottom),
            null
        )
    }

    fun draw(canvas: Canvas, destRect: Rect) {
        if (bitmap.isRecycled) return
        //TODO re-draw only valid intersection for performance
        canvas.drawBitmap(
            bitmap,
            Rect(0, 0, width, height),
            destRect,
            null
        )
    }
}
