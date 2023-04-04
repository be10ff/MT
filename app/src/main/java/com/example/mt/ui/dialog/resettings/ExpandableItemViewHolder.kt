package com.example.mt.ui.dialog.resettings

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mt.R

class ExpandableItemViewHolder (v: View) : RecyclerView.ViewHolder (v){
    val textView: TextView
    val icon: ImageView

    init {
        textView = v.findViewById(R.id.itemText)
        icon = v.findViewById(R.id.expandIcon)
    }
}
