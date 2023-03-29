package com.example.mt.ui.dialog

import com.example.mt.ui.dialog.settings.LayerHolderCallback
import com.example.mt.ui.dialog.settings.OnStartDragListener

interface IHolder {
    fun bind(callback: LayerHolderCallback, dragListener: OnStartDragListener)
    fun unBind()
}