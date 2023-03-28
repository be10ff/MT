package com.example.mt.ui.dialog.markers

import androidx.recyclerview.widget.RecyclerView

interface MarkerCallback {
    fun onSelectionChanged(holder: RecyclerView.ViewHolder)
}
