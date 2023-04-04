package com.example.mt.ui.dialog.resettings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mt.R
import com.example.mt.databinding.ExpandableDialogBinding
import com.example.mt.ui.dialog.AbstractDialog

class ExpandableDialog : AbstractDialog(/*R.layout.expandable_dialog*/) {
    val adapter = ExpadableAdapter()

    private var _binding: ExpandableDialogBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ExpandableDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun setupObserve() {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.FullScreenDialgStyle)
    }

    override fun setupGUI(savedInstanceState: Bundle?) {
        adapter.items = listOf(
            ExpandableItem("aaaa",  false, InnerItem("111111")),
            ExpandableItem("bbbb", false, InnerItem("22222")),
            CommonItem("cccccccc"),
            ExpandableItem("ddd",false, InnerItem("444444")),
            CommonItem("fffffff"),
        )

        binding.rvExpandable.layoutManager = LinearLayoutManager(requireContext())
        binding.rvExpandable.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object{
        const val TAG = "EXPANDABLE_DIALOG"
    }

}