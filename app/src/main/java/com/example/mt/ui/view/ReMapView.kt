package com.example.mt.ui.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.util.AttributeSet
import android.view.View

class ReMapView(
    context: Context?,
    attrs: AttributeSet
) : View(context, attrs) {

    var bitmap: Bitmap? = null

    override fun onDraw(canvas: Canvas?) {
        bitmap?.let {
            if (!it.isRecycled)
                canvas?.drawColor(Color.WHITE)
            canvas?.drawBitmap(it, 0f, 0f, null)
        }
    }

    fun reDraw(bitmap: Bitmap) {
        this.bitmap?.recycle()
        this.bitmap = bitmap
        invalidate()
    }
}