package com.example.mt.ui.dialog.info

import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mt.R
import com.example.mt.map.wkt.WktGeometry
import com.example.mt.ui.dialog.AbstractDialog
import kotlinx.android.synthetic.main.dialog_info.*

class InfoDialog(val geometry: WktGeometry) : AbstractDialog(R.layout.dialog_info) {

    private val callback = object : AttributesCallback{
        override fun onNameChanged(holder: RecyclerView.ViewHolder, value: String) {
            TODO("Not yet implemented")
        }

        override fun onValueChanged(holder: RecyclerView.ViewHolder, value: String) {
            TODO("Not yet implemented")
        }

    }

    private val adapter : AttributesAdapter = AttributesAdapter(callback)
    override fun setupObserve() {
    }

    override fun setupGUI(savedInstanceState: Bundle?) {
        dialog?.window?.let{ window ->
            window.setGravity(Gravity.BOTTOM)
            val params = window.attributes.apply {
                dimAmount = 0.05f
                height = context?.resources?.getDimension(R.dimen.info_dialog_height)?.toInt() ?: 240
                width = ViewGroup.LayoutParams.MATCH_PARENT
            }
            window.attributes = params

        }
        adapter.attributes = geometry.attributes.values.toMutableList()
        rvAttrs.layoutManager = LinearLayoutManager(requireContext())
        rvAttrs.adapter = adapter
    }

    companion object {
        val TAG = "INFO_DIALOG_TAG"
    }
}