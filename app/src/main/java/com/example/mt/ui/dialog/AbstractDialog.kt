package com.example.mt.ui.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.example.mt.ui.main.FragmentViewModel

abstract class AbstractDialog(
    protected val layoutId: Int = 0
) : DialogFragment() {

    val fragmentViewModel: FragmentViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return layoutId.takeIf { it != 0 }?.let { inflater.inflate(it, container) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupGUI(savedInstanceState)
        setupObserve()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState)
            .apply {
                window?.requestFeature(Window.FEATURE_NO_TITLE)
            }
    }

    abstract fun setupObserve()

    abstract fun setupGUI(savedInstanceState: Bundle?)
}