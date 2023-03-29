package com.example.mt.ui.dialog.info

import androidx.recyclerview.widget.RecyclerView
import com.example.mt.map.wkt.WktGeometry

interface GeometryInfoCallback {
    fun onEdit(geometry: WktGeometry?)
    fun onDelete(geometry: WktGeometry?)
    fun onSetPoi(geometry: WktGeometry?)
    fun onClose(geometry: WktGeometry?)
}