package com.example.mt.model.gi

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect

data class GIBitmap(
    val _bounds: GIBounds,
    val bitmap: Bitmap,
    val width: Int,
    val height: Int
) {
    constructor(bounds: GIBounds, width: Int, height: Int) :
            this(bounds, Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888), width, height)

    constructor(bounds: GIBounds, bitmap: Bitmap) : this(
        bounds,
        bitmap,
        bitmap.width,
        bitmap.height
    )

    fun draw(canvas: Canvas, bounds: GIBounds) {
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
}
