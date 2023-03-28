package com.example.mt.ui.dialog.info

import androidx.recyclerview.widget.RecyclerView

interface AttributesCallback {
    fun onNameChanged(holder: RecyclerView.ViewHolder, value: String)
    fun onValueChanged(holder: RecyclerView.ViewHolder, value: String)
}