package com.example.mt.ui.dialog.settings

import android.view.View
import androidx.appcompat.widget.SwitchCompat
import com.example.mt.R
import com.example.mt.model.xml.SqlProjection

class SQLLayerHolder(v: View) :
    LayerHolder(v) {

    val tileProjection: SwitchCompat

    init {
        tileProjection = v.findViewById(R.id.sTileProjection)
    }

    override fun bind(callback: LayerHolderCallback, dragListener: OnStartDragListener) {
        super.bind(callback, dragListener)
        tileProjection.setOnCheckedChangeListener { _, isChecked ->
            callback.onType(this, if (isChecked) SqlProjection.GOOGLE else SqlProjection.YANDEX)
        }
    }

    override fun unBind() {
        super.unBind()
        tileProjection.setOnCheckedChangeListener( null )
    }
}