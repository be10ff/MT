package com.example.mt.ui.dialog.settings

import android.view.View
import android.widget.ImageView
import androidx.appcompat.widget.SwitchCompat
import com.example.mt.R
import com.example.mt.model.xml.SqlProjection

class XMLLayerHolder(v: View, callback: LayerHolderCallback, dragListener: OnStartDragListener) :
    LayerHolder(v, callback, dragListener) {

//    val marker: ImageView

    init {
        marker.visibility = View.VISIBLE
//        marker = v.findViewById(R.id.ivMarker)
        marker.setOnClickListener {
            callback.onMarkersSource(this)
        }
    }

    override fun unBind() {
        super.unBind()
        marker.setOnClickListener(null)
    }
}