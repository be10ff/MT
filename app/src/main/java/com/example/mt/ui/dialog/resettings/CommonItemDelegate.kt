package com.example.mt.ui.dialog.resettings

import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.mt.R
import com.hannesdorfmann.adapterdelegates4.AbsListItemAdapterDelegate

class CommonItemDelegate : AbsListItemAdapterDelegate<CommonItem, ListItem, CommonItemViewHolder>() {

    override fun isForViewType(
        item: ListItem,
        items: MutableList<ListItem>,
        position: Int
    ): Boolean {
        return item is CommonItem
    }

    override fun onCreateViewHolder(parent: ViewGroup): CommonItemViewHolder {
        return LayoutInflater.from(parent.context)
            .inflate(R.layout.common_item, parent, false)
            .let{
                CommonItemViewHolder(it)
            }
//        return ExpandableItemViewHolder(inflateExpandableView(parent))
    }

    override fun onBindViewHolder(
        item: CommonItem,
        holder: CommonItemViewHolder,
        payloads: MutableList<Any>
    ) {
        holder.textView.text = item.text
    }

}
