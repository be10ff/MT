package com.example.mt.ui.view

import android.graphics.Point

interface ControlListener {
    fun moveViewBy(x: Int, y: Int)
    fun scaleViewBy(focus: Point, scaleFactor: Float)
    fun updateMap()

    fun clickAt(point: Point)
}