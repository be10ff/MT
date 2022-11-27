package com.example.mt.ui.view

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.view.SurfaceHolder
import com.example.mt.map.layer.GILayer
import com.example.mt.model.gi.GIBitmap

class DrawThread(private val surfaceHolder: SurfaceHolder) : Thread() {

    //    var bounds: GIBounds? = null
    var bitmap: GIBitmap? = null

    override fun run() {
        var canvas: Canvas? = null
//        Log.d("THREAD", "started" + this )
        try {
            canvas = surfaceHolder.lockCanvas()
            canvas?.let {
                val rect = canvas.clipBounds
                val tmpBitmap =
                    Bitmap.createBitmap(rect.width(), rect.height(), Bitmap.Config.ARGB_8888)
                        .apply {
                            eraseColor(Color.WHITE)
                        }
                bitmap?._bounds?.let { area ->
                    synchronized(surfaceHolder) {
//                            Log.d("THREAD", "redraw" + this )
                        GILayer.sqlTest.redraw(area, tmpBitmap, 0, 0.0)
                        canvas.drawBitmap(tmpBitmap, rect, rect, null)
                    }
                }
            }
        } finally {
            canvas?.let {
//                Log.d("THREAD", "finish" + this )
                surfaceHolder.unlockCanvasAndPost(it)
            }
        }

    }

    fun draw(bitmap: GIBitmap) {
        this.bitmap = bitmap
        start()
    }
}