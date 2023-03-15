package com.example.mt.ui.view

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.location.Location
import android.view.View
import android.view.ViewGroup
import com.example.mt.R
import com.example.mt.model.gi.GILonLat
import com.example.mt.model.gi.Project
import kotlin.math.acos
import kotlin.math.hypot

class PositionControl constructor(
    map: View,
    context: Context
) : View(context), IControl {


    val image = BitmapFactory.decodeResource(context.resources, R.drawable.position_arrow)
    val _matrix = Matrix()
    var originPosition: GILonLat? = null
    var currentPosition: GILonLat? = null
    var direction = -Math.PI / 2

    init {
        (map.parent as? ViewGroup)?.addView(this)
        bringToFront()
    }

    override val gpsConsumer: (Location?, Project) -> Unit = { location, project ->
        currentPosition?.let { current ->

            originPosition = current
        }
        location?.let {
            currentPosition = GILonLat(location)
            currentPosition?.let {
                project.mapToScreen(it)
            }?.let { point ->
//                val mapLocation = intArrayOf(0, 0)
//                map.getLocationOnScreen(mapLocation)
//                mapLocation[0] -= image.height/2 + map.offsetX()
//                mapLocation[1] -= image.width/2 + map.offsetY()
//                x = (point.x + mapLocation[0]).toFloat()
//                y = (point.y + mapLocation[1]).toFloat()
                x = (point.x - image.height / 2).toFloat()
                y = (point.y - image.width / 2).toFloat()
                /**/
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
                /**/
                invalidate()
            }
        }

    }

    override fun onDraw(canvas: Canvas?) {
//        originPosition?.let{origin ->
//            currentPosition?.let { current ->
//                val hypot = hypot(current.lon - origin.lon, current.lat - origin.lat)
//                if(hypot != 0.0){
//                    val dirCos = (current.lon - origin.lon)/hypot
//                    val dirSin = (current.lat - origin.lat)/hypot
//                    direction = Math.toDegrees(if(dirSin > 0) acos(dirCos) else -acos(dirCos))
//                }
//
//            }
//        }
        _matrix.reset()
        _matrix.setRotate(direction.toFloat(), image.width / 2f, image.height / 2f)
        canvas?.drawBitmap(image, _matrix, null)
    }
}
