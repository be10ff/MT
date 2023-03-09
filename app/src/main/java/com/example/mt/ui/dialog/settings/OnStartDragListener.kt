package com.example.mt.ui.dialog.settings

import androidx.recyclerview.widget.RecyclerView

interface OnStartDragListener {
    fun onStartDrag(viewHolder: RecyclerView.ViewHolder)
    fun onStartSwipe(viewHolder: RecyclerView.ViewHolder)
}