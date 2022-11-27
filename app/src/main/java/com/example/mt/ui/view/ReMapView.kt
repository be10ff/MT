package com.example.mt.ui.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View

class ReMapView(
    context: Context?,
    attrs: AttributeSet
) : View(context, attrs) {

    var bitmap: Bitmap? = null

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        bitmap?.let {
            if (!it.isRecycled)
                canvas?.drawBitmap(it, 0f, 0f, null)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        bitmap?.recycle()
        bitmap = if (w > 0 && h > 0) Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888) else null
//        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    }

    fun reDraw(bitmap: Bitmap) {
//        this.bitmap?.recycle()
        this.bitmap = bitmap
        invalidate()
    }
//    fun draw(bitmap: Bitmap) {
////        this.bitmap?.recycle()
//        this.bitmap = bitmap
//        invalidate()
//    }
}