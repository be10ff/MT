package com.example.mt.ui.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.example.mt.model.gi.GIBitmap
import com.example.mt.model.gi.GIBounds
import com.example.mt.model.gi.GIProjection

class GIMap(
    context: Context?

) : SurfaceView(context) {
    private var surfaceHolder: SurfaceHolder? = null
    private var bitmap: GIBitmap? = null
    private var bounds: GIBounds = GIBounds(GIProjection.WGS84, 0.0, 90.0, 90.0, 0.0)

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        surfaceHolder?.surface?.let { surface ->
            while (!surface.isValid) {

            }
            surfaceHolder?.lockCanvas()
                ?.let { canvas ->
                    canvas.drawColor(Color.TRANSPARENT)
                    bitmap?.draw(canvas, bounds)
                    surfaceHolder?.unlockCanvasAndPost(canvas)
                }

        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
    }
}