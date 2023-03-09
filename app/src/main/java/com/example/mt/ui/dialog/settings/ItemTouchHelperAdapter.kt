package com.example.mt.ui.dialog.settings

import androidx.recyclerview.widget.RecyclerView

interface ItemTouchHelperAdapter {
    fun onItemMove(from: RecyclerView.ViewHolder, to: RecyclerView.ViewHolder): Boolean
    fun onItemDismiss(holder: RecyclerView.ViewHolder)
}