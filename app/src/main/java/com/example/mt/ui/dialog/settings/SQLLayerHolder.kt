package com.example.mt.ui.dialog.settings

import android.view.View
import androidx.appcompat.widget.SwitchCompat
import com.example.mt.R
import com.example.mt.model.xml.SqlProjection

class SQLLayerHolder(v: View, callback: LayerHolderCallback, dragListener: OnStartDragListener) :
    LayerHolder(v, callback, dragListener) {

    val tileProjection: SwitchCompat

    init {
        tileProjection = v.findViewById(R.id.sTileProjection)

        tileProjection.setOnCheckedChangeListener { _, isChecked ->

            callback.onType(this, if (isChecked) SqlProjection.GOOGLE else SqlProjection.YANDEX)
        }
    }
}