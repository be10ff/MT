package com.example.mt.ui.view

import android.content.Context
import android.graphics.*
import android.location.Location
import android.util.AttributeSet
import android.view.View
import android.view.ViewTreeObserver
import androidx.core.graphics.applyCanvas
import com.example.mt.R
import com.example.mt.map.MapUtils
import com.example.mt.map.Screen
import com.example.mt.map.wkt.WktPoint
import com.example.mt.model.ControlState
import com.example.mt.model.SensorState
import com.example.mt.model.gi.GILonLat
import com.example.mt.model.gi.Project
import com.example.mt.model.gi.Projection
import kotlinx.android.synthetic.main.control_scale.view.*

class DirectionControll constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
//    defStyleAttr: Int = 0
) : View(context, attributeSet/*, defStyleAttr*/), IControl {

    var startMargin = 24f
    var arrowWidth = 24f
    var arrowLength =  120f
    var textSize = 24f
    var textColor = Color.BLACK
    var arrowColor = Color.LTGRAY

    val paintArrow: Paint
    val paintText: Paint

    val _matrix = Matrix()
    var direction = -Math.PI / 2
    var distance = 0
    var lastDist: String = ""

    val arrowBitmap: Bitmap
    val arrowPath: Path


    init{
        val a = context.obtainStyledAttributes(attributeSet, R.styleable.DirectionControll, 0, 0)
        startMargin = a.getDimension(R.styleable.DirectionControll_arrowWidth, 24f)
        arrowWidth = a.getDimension(R.styleable.DirectionControll_arrowWidth, 24f)
        arrowLength = a.getDimension(R.styleable.DirectionControll_arrowLength, 120f)
        textSize = a.getDimension(R.styleable.DirectionControll_fontSize, 24f)
        textColor = a.getColor(R.styleable.DirectionControll_textColor, Color.BLACK)
        arrowColor = a.getColor(R.styleable.DirectionControll_arrowColor,resources.getColor( R.color.clean_green))
        a.recycle()

        paintArrow = Paint().apply {
            shader = LinearGradient( 2*startMargin, 0f, arrowLength, arrowWidth, Color.TRANSPARENT, arrowColor, Shader.TileMode.CLAMP)

        }

        paintText = Paint().apply {
            color = textColor
            style = Paint.Style.FILL
            textSize = this@DirectionControll.textSize
        }

        arrowBitmap = Bitmap.createBitmap(arrowLength.toInt(), arrowWidth.toInt(), Bitmap.Config.ARGB_8888)
        arrowPath = Path().apply {
            moveTo(startMargin,0f)
            lineTo(arrowLength - arrowWidth, 0f)
            lineTo(arrowLength, arrowWidth/2)
            lineTo(arrowLength - arrowWidth, arrowWidth)
            lineTo(arrowWidth,startMargin)
            lineTo(startMargin,0f)
        }

        viewTreeObserver.addOnGlobalLayoutListener ( object : ViewTreeObserver.OnGlobalLayoutListener{
            override fun onGlobalLayout() {
                viewTreeObserver.removeOnGlobalLayoutListener(this)

            }
        })
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        bringToFront()
    }

    private fun drawArrow(distance: String) : Bitmap {
        return arrowBitmap.applyCanvas {

            drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
//            drawPoint(0f, arrowWidth/2, paintArrow)

            drawPath(arrowPath, paintArrow)

            drawText(distance, 2*startMargin, arrowWidth/2 + paintText.textSize/2 , paintText)

        }
    }

    override fun consume(state: ControlState) {
        state.sensorState.location?.let{ location ->
            state.selection?.point?.let { point ->
                this@DirectionControll.visibility = VISIBLE
//            val center = Projection.reproject(state.project.bounds.center, Projection.WGS84)
                val center = GILonLat(location)
                distance = MapUtils.getDistance(center, point).toInt()
                direction = MapUtils.getAzimuth(center, point)
                val d = when {
                    distance <= 1000 -> {
                        "$distance m"
                    }
                    distance > 1000 && distance < 1000000 -> {
                        "${distance / 1000} km"
                    }
                    else -> {
                        "${distance / 1000000}K km"
                    }
                }
                if (lastDist != d) {
                    drawArrow(d)
                    invalidate()
                    lastDist = d
                }
            }

        } ?: run{this@DirectionControll.visibility = GONE}
    }

    override fun onDraw(canvas: Canvas?) {
        canvas?.let{
            _matrix.reset()
            _matrix.postTranslate(width / 2f, height / 2f - arrowWidth / 2)
            _matrix.postRotate(direction.toFloat() - 90f, width / 2f, height / 2f)

            canvas.drawBitmap(arrowBitmap, _matrix, null)
        }
    }
}