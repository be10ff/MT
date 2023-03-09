package com.example.mt.ui.dialog.settings

import android.graphics.Canvas
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.abs

class LayerTouchHelperCallback(val adapter: ItemTouchHelperAdapter) : ItemTouchHelper.Callback() {

    override fun isLongPressDragEnabled(): Boolean = true

    override fun isItemViewSwipeEnabled(): Boolean = false

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        return when {
            viewHolder.itemViewType == SettingsAdapter.TYPE_GROUP -> 0
            else -> {
                val dragFlag = ItemTouchHelper.UP or ItemTouchHelper.DOWN
                val swipeFlag = ItemTouchHelper.START or ItemTouchHelper.END
                makeMovementFlags(dragFlag, swipeFlag)
            }
        }
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return when {
            viewHolder.itemViewType == SettingsAdapter.TYPE_GROUP || target.itemViewType == SettingsAdapter.TYPE_GROUP -> false
            else -> adapter.onItemMove(viewHolder, target)
        }
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        if (viewHolder.itemViewType != SettingsAdapter.TYPE_GROUP)
            adapter.onItemDismiss(viewHolder)
    }

    override fun onChildDrawOver(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder?,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            viewHolder?.let { holder ->
                val alpha = ALPHA_FULL - abs(dX) / holder.itemView.width
                holder.itemView.alpha = alpha
                holder.itemView.translationX = dX
            }
        } else super.onChildDrawOver(
            c,
            recyclerView,
            viewHolder,
            dX,
            dY,
            actionState,
            isCurrentlyActive
        )
    }

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) (viewHolder as? ItemTouchHelperViewHolder)?.onItenSelected()
        super.onSelectedChanged(viewHolder, actionState)
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)
        viewHolder.itemView.alpha = ALPHA_FULL
        (viewHolder as? ItemTouchHelperViewHolder)?.onItemClear()
    }

    companion object {
        val ALPHA_FULL = 1f
    }
}