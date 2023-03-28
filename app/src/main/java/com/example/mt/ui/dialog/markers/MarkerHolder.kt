package com.example.mt.ui.dialog.markers

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mt.R

class MarkerHolder(view: View, callback: MarkerCallback) : RecyclerView.ViewHolder(view) {

    val root: ViewGroup
    val name: TextView

    init{
        name = view.findViewById(R.id.tvName)
        root = view.findViewById(R.id.root)

        root.setOnClickListener {
            callback.onSelectionChanged(this)
        }
    }

}
