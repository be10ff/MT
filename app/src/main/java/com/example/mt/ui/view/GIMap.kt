package com.example.mt.ui.view

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.example.mt.model.gi.GIBounds
import com.example.mt.model.gi.GIProjection

class GIMap(
    context: Context?,
    attrs: AttributeSet
) : SurfaceView(context, attrs), SurfaceHolder.Callback {
    //    private var bitmap: GIBitmap? = null
    private var bounds: GIBounds = GIBounds(GIProjection.WGS84, 0.0, 90.0, 90.0, 0.0)
    private var thread: DrawThread? = null


    init {
        holder.addCallback(this)

    }

    private fun adjustBoundsRatio(bounds: GIBounds): GIBounds {
        val scrRatio = width.toDouble() / height
        val areaRatio = bounds.width / bounds.height
        return when {
            areaRatio > scrRatio -> {
                val diff = (bounds.width / scrRatio - bounds.height) / 2
                GIBounds(
                    bounds.projection,
                    bounds.left,
                    bounds.top + diff,
                    bounds.right,
                    bounds.bottom - diff
                )
            }
            areaRatio < scrRatio -> {
                val diff = (bounds.height * scrRatio - bounds.width) / 2
                GIBounds(
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

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

//        surfaceHolder?.surface?.let { surface ->
//            while (!surface.isValid) {
//
//            }
//            surfaceHolder?.lockCanvas()
//                ?.let { canvas ->
//                    canvas.drawColor(Color.TRANSPARENT)
//                    bitmap?.draw(canvas, bounds)
//                    surfaceHolder?.unlockCanvasAndPost(canvas)
//                }
//
//        }
    }


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        bounds = adjustBoundsRatio(
            GIBounds(
                GIProjection.WGS84,
                28.0,
                65.0,
                48.0,
                46.0
            ).reproject(GIProjection.WorldMercator)
        )
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        thread = DrawThread(holder, bounds)
        thread?.start()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {

    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        thread?.join()
    }

}