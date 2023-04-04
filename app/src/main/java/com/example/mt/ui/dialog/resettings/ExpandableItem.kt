package com.example.mt.ui.dialog.resettings

data class ExpandableItem(
    val text: String,
    val isExpanded: Boolean,
    val innerItem: InnerItem
) : ListItem {

    override fun equals(other: Any?): Boolean {
        if(other !is ExpandableItem) return false
        if(this === other) return true
        if(text == other.text && innerItem == other.innerItem) return true
        return super.equals(other)
        return false
    }
}
