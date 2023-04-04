package com.example.mt.ui.dialog.resettings

import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.mt.R
import com.hannesdorfmann.adapterdelegates4.AbsListItemAdapterDelegate

class InnerItemDelegate : AbsListItemAdapterDelegate<InnerItem, ListItem, InnerItemViewHolder>() {

    override fun isForViewType(
        item: ListItem,
        items: MutableList<ListItem>,
        position: Int
    ): Boolean {
        return item is InnerItem
    }

    override fun onCreateViewHolder(parent: ViewGroup): InnerItemViewHolder {
        return LayoutInflater.from(parent.context)
            .inflate(R.layout.inner_item, parent, false)
            .let{
                InnerItemViewHolder(it)
            }
//        return ExpandableItemViewHolder(inflateExpandableView(parent))
    }

    override fun onBindViewHolder(
        item: InnerItem,
        holder: InnerItemViewHolder,
        payloads: MutableList<Any>
    ) {
        holder.textView.text = item.text
    }

}
