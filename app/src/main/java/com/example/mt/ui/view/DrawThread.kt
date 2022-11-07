package com.example.mt.ui.view

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.view.SurfaceHolder
import com.example.mt.map.layer.GILayer
import com.example.mt.model.gi.GIBounds

class DrawThread(val surfaceHolder: SurfaceHolder, val bounds: GIBounds) : Thread() {

    override fun run() {
        val canvas: Canvas = surfaceHolder.lockCanvas()
        val rect = canvas.clipBounds
        val tmpBitmap =
            Bitmap.createBitmap(rect.width(), rect.height(), Bitmap.Config.ARGB_8888).apply {
                eraseColor(Color.WHITE)
            }
        synchronized(surfaceHolder) {
            GILayer.sqlTest.redraw(bounds, tmpBitmap, 0, 0.0)
            canvas.drawBitmap(tmpBitmap, rect, rect, null)
        }
        surfaceHolder.unlockCanvasAndPost(canvas)
    }
}