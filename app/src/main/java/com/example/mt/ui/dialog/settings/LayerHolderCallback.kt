package com.example.mt.ui.dialog.settings

import androidx.recyclerview.widget.RecyclerView
import com.example.mt.model.xml.SqlProjection

interface LayerHolderCallback {
    fun onVisibilityChanged(holder: RecyclerView.ViewHolder, isChecked: Boolean)
    fun onRangeChanging(holder: RecyclerView.ViewHolder, min: Float, max: Float)
    fun onRemove(holder: RecyclerView.ViewHolder)
    fun onMove(from: RecyclerView.ViewHolder, to: RecyclerView.ViewHolder)
    fun onType(holder: RecyclerView.ViewHolder, type: SqlProjection)
}