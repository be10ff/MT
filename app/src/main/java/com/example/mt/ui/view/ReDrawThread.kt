package com.example.mt.ui.view

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import com.example.mt.map.layer.GILayer
import com.example.mt.model.gi.GIBitmap

class ReDrawThread(val bitmap: GIBitmap?) {

    suspend fun draw() {
        bitmap?.bitmap?.let {
            Canvas(it)
        }?.let { canvas ->
            val rect = canvas.clipBounds
            val tmpBitmap =
                Bitmap.createBitmap(rect.width(), rect.height(), Bitmap.Config.ARGB_8888)
                    .apply {
                        eraseColor(Color.WHITE)
                    }
            bitmap._bounds.let { area ->
                GILayer.sqlTest.redraw(area, tmpBitmap, 0, 0.0)
                canvas.drawBitmap(tmpBitmap, rect, rect, null)
            }
        }

    }
}