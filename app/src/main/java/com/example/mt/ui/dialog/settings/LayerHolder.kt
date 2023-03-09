package com.example.mt.ui.dialog.settings

import android.view.MotionEvent
import android.view.View
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mt.R
import com.github.guilhe.views.SeekBarRangedView
import com.github.guilhe.views.SeekBarRangedView.SeekBarRangedChangeCallback
import kotlin.math.roundToInt

open class LayerHolder(v: View, callback: LayerHolderCallback, dragListener: OnStartDragListener) :
    RecyclerView.ViewHolder(v) {
    val filePath: TextView
    val exist: ImageView
    val enabled: CheckBox
    val range: SeekBarRangedView
    val rangeMin: TextView
    val rangeMax: TextView
    val marker: ImageView
    val details: ImageView
    val reOrder: ImageView
    val delete: ImageView

    init {
        filePath = v.findViewById(R.id.tvFilePath)
        exist = v.findViewById(R.id.ivFileExist)
        enabled = v.findViewById(R.id.ivEnabled)
        range = v.findViewById(R.id.sbRange)
        rangeMin = v.findViewById(R.id.tvRangeMin)
        rangeMax = v.findViewById(R.id.tvRangeMax)
        marker = v.findViewById(R.id.ivMarker)
        details = v.findViewById(R.id.ivDetails)
        reOrder = v.findViewById(R.id.ivReorder)
        delete = v.findViewById(R.id.ivDelete)

        enabled.setOnCheckedChangeListener { _, isChecked ->
            callback.onVisibilityChanged(this, isChecked)
        }
        range.actionCallback = object : SeekBarRangedChangeCallback {
            override fun onChanged(minValue: Float, maxValue: Float) {
                callback.onRangeChanging(this@LayerHolder, minValue, maxValue)
            }

            override fun onChanging(minValue: Float, maxValue: Float) {
                rangeMin.text = minValue.roundToInt().toString()
                rangeMax.text = maxValue.roundToInt().toString()
            }

        }
        reOrder.setOnTouchListener { v, event ->
            if (event.actionMasked == MotionEvent.ACTION_DOWN) dragListener.onStartDrag(this)
            false
        }

        delete.setOnTouchListener { v, event ->
            if (event.actionMasked == MotionEvent.ACTION_DOWN) dragListener.onStartSwipe(this)
            false
        }
    }
}