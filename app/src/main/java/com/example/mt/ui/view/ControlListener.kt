package com.example.mt.ui.view

import android.graphics.Point

interface ControlListener {
    fun onSetToDraft(b: Boolean)
    fun moveViewBy(x: Int, y: Int)
    fun scaleViewBy(focus: Point, scaleFactor: Float)
    fun updateMap()
}