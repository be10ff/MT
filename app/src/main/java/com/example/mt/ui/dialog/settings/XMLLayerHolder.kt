package com.example.mt.ui.dialog.settings

import android.view.View
import android.widget.ImageView
import androidx.appcompat.widget.SwitchCompat
import com.example.mt.R
import com.example.mt.model.xml.SqlProjection

class XMLLayerHolder(v: View) :
    LayerHolder(v) {

//    val marker: ImageView

    init {
        marker.visibility = View.VISIBLE
    }

    override fun bind(callback: LayerHolderCallback, dragListener: OnStartDragListener) {
        super.bind(callback, dragListener)
        marker.setOnClickListener {
            callback.onMarkersSource(this)
        }
    }

    override fun unBind() {
        super.unBind()
        marker.setOnClickListener(null)
    }
}