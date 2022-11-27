package com.example.mt.ui.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Rect
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.example.mt.model.gi.GIBitmap
import com.example.mt.model.gi.GIBounds

class MapView(
    context: Context?,
    attrs: AttributeSet
) : SurfaceView(context, attrs), SurfaceHolder.Callback {
    private var thread: DrawThread? = null
    private var bitmap: GIBitmap? = null
    lateinit var listener: ControlListener

    init {
        holder.addCallback(this)
    }

    fun reDraw(bounds: GIBounds) {
        thread?.join()
        thread = null
        holder?.surface?.let { surface ->
//            var i = 0
//            while (!surface.isValid){
//                i++
//            }
            if (surface.isValid) {
                val canvas = surface.lockCanvas(null)
                canvas.drawColor(Color.WHITE)
                bitmap?.draw(canvas, bounds)
                holder.unlockCanvasAndPost(canvas)
            }
        }
    }

    fun draw(bitmap: Bitmap) {
        holder?.surface?.let { surface ->
//            var i = 0
//            while (!surface.isValid){
//                i++
//            }
            if (surface.isValid) {
                val canvas = surface.lockCanvas(null)
                canvas.drawColor(Color.WHITE)
                canvas.drawBitmap(bitmap, 0f, 0f, null)
                holder.unlockCanvasAndPost(canvas)
            }
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
//        val bounds = adjustBoundsRatio(
//            GIBounds(
//                GIProjection.WGS84,
//                28.0,
//                65.0,
//                48.0,
//                46.0
//            ).reproject(GIProjection.WorldMercator)
//        )
        listener.viewRectChanged(rect = Rect(left, top, right, bottom))
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
//        thread = DrawThread(holder)
//        thread?.start()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {

    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        thread?.join()
        thread = null
    }

    fun update(bounds: GIBounds) {
//        thread = DrawThread(holder)
//        bitmap =  GIBitmap(bounds, width, height)
//            .also {
//                thread?.draw(it, bounds)
//            }

//        thread?.start()


//        holder?.surface?.let { surface ->
//            while (!surface.isValid) {
//                val res = 0
//            }
//            thread = DrawThread(holder, bounds)
//            thread?.start()
//
//        }
    }

}