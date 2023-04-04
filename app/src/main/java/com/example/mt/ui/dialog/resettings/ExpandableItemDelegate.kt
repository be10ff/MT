package com.example.mt.ui.dialog.resettings

import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.mt.R
import com.hannesdorfmann.adapterdelegates4.AbsListItemAdapterDelegate

class ExpandableItemDelegate(
    private val clickCallback: (expandableItem : ExpandableItem) -> Unit
) : AbsListItemAdapterDelegate<ExpandableItem, ListItem, ExpandableItemViewHolder>() {

    override fun isForViewType(
        item: ListItem,
        items: MutableList<ListItem>,
        position: Int
    ): Boolean {
        return item is ExpandableItem
    }

    override fun onCreateViewHolder(parent: ViewGroup): ExpandableItemViewHolder {
        return LayoutInflater.from(parent.context)
            .inflate(R.layout.expandable_item, parent, false)
            .let{
                ExpandableItemViewHolder(it)
            }
    }

    override fun onBindViewHolder(
        item: ExpandableItem,
        holder: ExpandableItemViewHolder,
        payloads: MutableList<Any>
    ) {
        holder.icon.setOnClickListener {
            clickCallback(item)
        }
        holder.textView.text = item.text
        holder.icon.rotation = if(item.isExpanded) 180f else 0f
    }

}
