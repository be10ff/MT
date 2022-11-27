package com.example.mt.ui.view

import android.graphics.Point
import android.graphics.Rect
import com.example.mt.model.gi.GIBounds

interface ControlListener {
    fun onSetToDraft(b: Boolean)
    fun moveViewBy(x: Int, y: Int)
    fun scaleViewBy(focus: Point, scaleFactor: Float)

    //    fun invalidate()
    fun updateMap()
    fun boundsChanged(bounds: GIBounds)
    fun viewRectChanged(rect: Rect)

}