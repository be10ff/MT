package com.example.mt.ui.view

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.location.Location
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import com.example.mt.R
import com.example.mt.map.wkt.WktPoint
import com.example.mt.model.ControlState
import com.example.mt.model.SensorState
import com.example.mt.model.gi.GILonLat
import com.example.mt.model.gi.Project
import kotlin.math.acos
import kotlin.math.hypot

class PositionControl constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs), IControl {


    val image = BitmapFactory.decodeResource(context.resources, R.drawable.position_arrow)
    val _matrix = Matrix()
    var originPosition: GILonLat? = null
    var currentPosition: GILonLat? = null
    var direction = -Math.PI / 2

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        bringToFront()
    }

    override fun consume(state: ControlState) {
        currentPosition?.let { current ->
            originPosition = current
        }
        state.sensorState.location?.let {
            currentPosition = GILonLat(it)
            currentPosition?.let {
                state.project.mapToScreen(it)
            }?.let { point ->
                x = (point.x - image.height / 2).toFloat()
                y = (point.y - image.width / 2).toFloat()
                originPosition?.let { origin ->
                    currentPosition?.let { current ->
                        val hypot = hypot(current.lon - origin.lon, current.lat - origin.lat)
                        if (hypot != 0.0) {
                            val dirCos = (current.lon - origin.lon) / hypot
                            val dirSin = (current.lat - origin.lat) / hypot
                            direction =
                                Math.toDegrees(if (dirSin > 0) acos(dirCos) else -acos(dirCos))
                        }

                    }
                }
                invalidate()
            }
        }
    }

    override fun onDraw(canvas: Canvas?) {
        _matrix.reset()
        _matrix.setRotate(direction.toFloat(), image.width / 2f, image.height / 2f)
        canvas?.drawBitmap(image, _matrix, null)
    }
}
