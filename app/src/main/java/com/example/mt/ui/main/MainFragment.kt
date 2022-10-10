package com.example.mt.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.mt.R
import kotlinx.android.synthetic.main.main_fragment.*

class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    //    private val uiViewModel: UIViewModel by activityViewModels()
    private val mainViewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObserve()
        setupGUI()
    }

    private fun setupObserve() {
//        uiViewModel.mainState.observe(viewLifecycleOwner){ mainState ->
//            message.text = mainState.buttonState.toString()
//        }

        mainViewModel.gpsState.observe(viewLifecycleOwner) { location ->
            btn_gps.text = location.toString()
        }
    }

    private fun setupGUI() {
//        btn.setOnClickListener {
//            uiViewModel.submitAction(UIAction.ToggleGps)
//        }
    }

}