package com.example.mt.ui.dialog.resettings

import androidx.recyclerview.widget.DiffUtil

class CommonCallBackImpl(val oldItems: List<ListItem>, val newItems: List<ListItem>) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldItems.size
    override fun getNewListSize(): Int = newItems.size


    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val newItem = newItems[newItemPosition]
        val oldItem = oldItems[oldItemPosition]
        return when{
            newItem is ExpandableItem && oldItem is ExpandableItem ->
                newItem.text == oldItem.text && newItem.innerItem == oldItem.innerItem
            else -> newItem == oldItem
        }
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val newItem = newItems[newItemPosition]
        val oldItem = oldItems[oldItemPosition]
        return when{
            newItem is ExpandableItem && oldItem is ExpandableItem ->
                newItem.text == oldItem.text && newItem.innerItem == oldItem.innerItem  /*&& newItem.isExpanded == oldItem.isExpanded*/
            else -> newItem == oldItem
        }
//        return newItem == oldItem
    }

//    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
//        return super.getChangePayload(oldItemPosition, newItemPosition)
//    }
}
