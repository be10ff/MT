package com.example.mt.ui.dialog.markers

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mt.R
import com.example.mt.databinding.DialogMarkersBinding
import com.example.mt.map.layer.XMLLayer
import com.example.mt.map.wkt.WktPoint
import com.example.mt.model.Action
import com.example.mt.model.xml.EditableType
import com.example.mt.ui.dialog.AbstractDialog
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class MarkersDialog : AbstractDialog(/*R.layout.dialog_markers*/) {

//    private var project: Project? = null

    private var _binding: com.example.mt.databinding.DialogMarkersBinding? = null
    private val binding get() = _binding!!

    private val callback = object : MarkerCallback{
        override fun onSelectionChanged(holder: RecyclerView.ViewHolder) {
            val point: WktPoint = adapter.getMarker(holder)
            fragmentViewModel.submitAction(Action.ProjectAction.MarkersSelectionChanged(point))
        }
    }

    private val adapter : MarkersAdapter = MarkersAdapter(callback)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogMarkersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun setupObserve() {
        lifecycleScope.launch {
            fragmentViewModel.projectState.let{ project ->
                    project.map {  it.layers.filterIsInstance<XMLLayer>().filter { it.isMarkersSource && it.editableType == EditableType.POI } }
                .collect {
                    it.map {
                        it.geometries.toList()
                    }.flatten()
                        .also{
                            adapter.mark = fragmentViewModel.markerGeometryState.value to it.filterIsInstance<WktPoint>()
                        }
                }
            }

        }
    }

    override fun setupGUI(savedInstanceState: Bundle?) {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        binding.rvMarkers.layoutManager = LinearLayoutManager(requireContext())
        binding.rvMarkers.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        val TAG = "MARKERS_DIALOG_TAG"
    }
}