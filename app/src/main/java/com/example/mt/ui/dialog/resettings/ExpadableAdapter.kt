package com.example.mt.ui.dialog.resettings

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.DiffUtil.calculateDiff
import com.hannesdorfmann.adapterdelegates4.ListDelegationAdapter

class ExpadableAdapter : ListDelegationAdapter<List<ListItem>>() {
//    private var sourceList: List<ListItem> = emptyList()

    init{
        delegatesManager.addDelegate(CommonItemDelegate())

        delegatesManager.addDelegate(ExpandableItemDelegate{ item ->
            val newSourceList = items?.toMutableList() ?: mutableListOf()
            val itemIndex =
//                items?.indexOfFirst {
//                    (it as? ExpandableItem)?.let {
//                        it.text == item.text
//                    } ?: false
//                } ?: -1
                items?.indexOf(item) ?: -1
                newSourceList[itemIndex] = item.copy(isExpanded = !item.isExpanded)
                setItems(newSourceList)

        })
        delegatesManager.addDelegate(InnerItemDelegate())
    }

    override fun setItems(sourceList: List<ListItem>?) {
//        this.sourceList = sourceList ?: emptyList()
//        val oldItems = items ?: emptyList()
//        val newItems = sourceList?.expand() ?: emptyList()
//        val diffResult: DiffUtil.DiffResult = calculateDiff(oldItems, newItems)
//        diffResult.dispatchUpdatesTo(this)
//        super.setItems(newItems)

        //
//        this.sourceList = sourceList ?: emptyList()
        val newItems = sourceList?.expand() ?: emptyList()
        super.setItems(newItems)
        notifyDataSetChanged()

    }

    private fun calculateDiff(
        oldItems: List<ListItem>,
        newItems: List<ListItem>
    ) : DiffUtil.DiffResult {
        //53:37
        val commonCallbackImpl = CommonCallBackImpl(
            oldItems = oldItems,
            newItems = newItems
        )
        return DiffUtil.calculateDiff(commonCallbackImpl)
    }

    private fun List<ListItem>.expand() =
        this.also{
            val res = it
        }
            .flatMap { item ->

        if(item is ExpandableItem && item.isExpanded)
            listOf(item) + item.innerItem
        else
            listOf(item)
    }
        .also{
            val res = it
        }


}