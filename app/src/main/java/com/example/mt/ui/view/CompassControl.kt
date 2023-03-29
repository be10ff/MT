package com.example.mt.ui.view

import android.content.Context
import android.graphics.*
import android.location.Location
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import androidx.core.graphics.applyCanvas
import com.example.mt.R
import com.example.mt.map.MapUtils
import com.example.mt.map.wkt.WktPoint
import com.example.mt.model.ControlState
import com.example.mt.model.SensorState
import com.example.mt.model.gi.GILonLat
import com.example.mt.model.gi.Project
import com.example.mt.model.gi.Projection
import kotlinx.android.synthetic.main.control_scale.view.*

class CompassControl constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
) : View(context, attributeSet), IControl {

    var startMargin = 24f
    var arrowWidth = 24f
    var arrowLength =  120f
    var arrowColor = Color.LTGRAY

    val _matrix = Matrix()
    var direction = 0f

    val arrowBitmap: Bitmap


    init{
        val a = context.obtainStyledAttributes(attributeSet, R.styleable.CompassControl, 0, 0)
        startMargin = a.getDimension(R.styleable.CompassControl_arrowWidth, 24f)
        arrowWidth = a.getDimension(R.styleable.CompassControl_arrowWidth, 24f)
        arrowLength = a.getDimension(R.styleable.CompassControl_arrowLength, 120f)
        arrowColor = a.getColor(R.styleable.CompassControl_arrowColor,resources.getColor( R.color.orange))
        a.recycle()

        val paintArrow = Paint().apply {
            shader = LinearGradient( 2*startMargin, 0f, arrowLength, arrowWidth, Color.TRANSPARENT, arrowColor, Shader.TileMode.CLAMP)
        }
        val arrowPath = Path().apply {
            moveTo(startMargin,0f)
            lineTo(arrowLength - arrowWidth, 0f)
            lineTo(arrowLength, arrowWidth/2)
            lineTo(arrowLength - arrowWidth, arrowWidth)
            lineTo(arrowWidth,startMargin)
            lineTo(startMargin,0f)
        }

        arrowBitmap = Bitmap.createBitmap(arrowLength.toInt(), arrowWidth.toInt(), Bitmap.Config.ARGB_8888)
            .applyCanvas {
                drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
                drawPoint(0f, arrowWidth/2, paintArrow)
                drawPath(arrowPath, paintArrow)
            }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        bringToFront()
    }

    override fun consume(state: ControlState) {
        state.selection?.point?.let { _ ->
            this@CompassControl.visibility = VISIBLE
            direction = -Math.toDegrees(state.sensorState.orientations.azimuth.toDouble()).toFloat()
            invalidate()
        } ?: run{this@CompassControl.visibility = GONE}
    }

    override fun onDraw(canvas: Canvas?) {
        canvas?.let{
            _matrix.reset()
            _matrix.postTranslate(width / 2f, height / 2f - arrowWidth / 2)
            _matrix.postRotate(direction, width / 2f, height / 2f)
            canvas.drawBitmap(arrowBitmap, _matrix, null)
        }
    }
}