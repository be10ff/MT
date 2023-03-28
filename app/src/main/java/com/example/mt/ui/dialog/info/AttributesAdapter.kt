package com.example.mt.ui.dialog.info

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mt.R
import com.example.mt.map.wkt.DBaseField

class AttributesAdapter(
    private val callback: AttributesCallback
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var attributes = emptyList <DBaseField>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return AttributesHolder(
        LayoutInflater.from(parent.context)
            .inflate(R.layout.item_attributes_list, parent, false), callback)
    }

    override fun getItemCount(): Int = attributes.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as AttributesHolder).also{
            attributes[position].let{ attr ->
                holder.name.setText(attr.name)
                holder.value.setText(attr.value)
            }
        }
    }

}
