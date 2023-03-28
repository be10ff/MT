package com.example.mt.ui.dialog.markers

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mt.R
import com.example.mt.map.layer.Layer
import com.example.mt.map.wkt.WktPoint
import com.example.mt.model.gi.Project

class MarkersAdapter(
    private val callback: MarkerCallback
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var markers = emptyList<WktPoint>()
    private var current : WktPoint? = null
    var mark : Pair<WktPoint?, List<WktPoint>> = Pair(null,emptyList<WktPoint>())
    set(value) {
        current = value.first
        markers = value.second
        field = value
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MarkerHolder(
        LayoutInflater.from(parent.context)
            .inflate(R.layout.item_marker_list, parent, false), callback)
    }

    fun getMarker(holder: RecyclerView.ViewHolder): WktPoint = markers[holder.adapterPosition]

    override fun getItemCount(): Int = markers.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MarkerHolder).also{
            markers[position].let{ point ->
                holder.name.setTextColor(holder.name.context.getColor(if(point == current) R.color.clean_green else R.color.control_text_button_bg))
                holder.name.text = point.attributes.values.firstOrNull()?.value
            }
        }
    }

}
