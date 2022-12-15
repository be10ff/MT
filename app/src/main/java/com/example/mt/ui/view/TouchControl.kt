package com.example.mt.ui.view

import android.content.Context
import android.graphics.Point
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener
import android.view.View
import android.view.View.OnLongClickListener
import android.view.ViewConfiguration
import kotlin.math.hypot

class TouchControl(
    context: Context?,
    attrs: AttributeSet
) : View(context, attrs), OnLongClickListener {
    val touchSlop = ViewConfiguration.get(context).scaledTouchSlop * 2

    var scaleFactor: Float = 1f

    var scaled: Boolean = false

    var focus: Point = Point()

    var multyClick: Boolean = false
    var moveClick: Boolean = false
    var longClick: Boolean = false
    var click: Boolean = false

    var originPointX: Float = 0f
    var originPointY: Float = 0f

    var previousX: Float = 0f
    var previousY: Float = 0f

    var activeId: Int = -1

    lateinit var listener: ControlListener

    private val scaleListener = object : SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector?): Boolean {
            scaleFactor = detector?.scaleFactor ?: 1f
            if (!scaled) {
                focus.x = detector?.focusX?.toInt() ?: 0
                focus.y = detector?.focusY?.toInt() ?: 0
                scaled = true
            }
            return true
        }
    }

    private val scaleDetector = ScaleGestureDetector(context, scaleListener)

    override fun onLongClick(v: View?): Boolean {
        return false
    }

    override fun onTouchEvent(motionEvent: MotionEvent?): Boolean {
        super.onTouchEvent(motionEvent)
        scaleDetector.onTouchEvent(motionEvent)
        motionEvent?.let { event ->
            val x = event.x
            val y = event.y


            val action = event.action
            when (action.and(MotionEvent.ACTION_MASK)) {
                MotionEvent.ACTION_POINTER_DOWN -> {
                    multyClick = true
                    moveClick = true
                    listener.onSetToDraft(false)
                }
                MotionEvent.ACTION_DOWN -> {
                    super.performClick()
                    originPointX = x
                    originPointY = y
                    multyClick = false
                    moveClick = false
                    longClick = false
                    click = true
                    listener.onSetToDraft(false)
                    previousX = x
                    previousY = y
                    activeId = event.getPointerId(0)
                }
                MotionEvent.ACTION_MOVE -> {
                    Log.d("TOUCH", "action move " + this)
                    if (touchSlop > hypot(originPointX - x, originPointY - y)) return false

                    val pointerIndex = event.findPointerIndex(activeId)
                    if (!scaled) {
                        val pointerX = event.getX(pointerIndex)
                        val pointerY = event.getY(pointerIndex)
                        //todo
                        val scale = /*map.viewRect.width/map.view.width*/ 1f
                        listener.moveViewBy(
                            (scale * (previousX - pointerX)).toInt(),
                            ((pointerY - previousY) * scale).toInt()
                        )
                        previousX = pointerX
                        previousY = pointerY
                    } else if (event.pointerCount == 2) {
                        listener.scaleViewBy(focus, scaleFactor)
                    }
//                    listener.invalidate()

                }
                MotionEvent.ACTION_UP -> {
                    if (!moveClick) return true
                    if (scaled) scaled = false
                    scaleFactor = 1f
                    listener.onSetToDraft(true)
                    listener.updateMap()
                }
                MotionEvent.ACTION_CANCEL -> {
                    activeId = -1
                }
                MotionEvent.ACTION_POINTER_UP -> {
                    val pointerIndex =
                        (event.action and MotionEvent.ACTION_POINTER_INDEX_MASK) shr MotionEvent.ACTION_POINTER_INDEX_SHIFT
                    val pointerId = event.getPointerId(pointerIndex)
                    if (pointerId == activeId) {
                        val newPointerIndex = if (pointerIndex == 0) 1 else 0
                        previousX = event.getX(newPointerIndex)
                        previousY = event.getY(newPointerIndex)
                        activeId = event.getPointerId(newPointerIndex)
                    }
                }
            }
        }
        return true
    }
}