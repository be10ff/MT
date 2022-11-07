package com.example.mt.ui.main

import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.example.mt.R

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

        mainViewModel.gpsState.observe(viewLifecycleOwner) { location ->

        }
        mainViewModel.gpsState.observe(viewLifecycleOwner, gpsObserver)

        mainViewModel.storageState.observe(viewLifecycleOwner) { granted ->

        }

    }

    private fun setupGUI() {

    }

    private val gpsObserver: Observer<Location?> = Observer { location ->
        location?.let { location ->
//            btn_gps.text = location.toString()
        } ?: run {
//            btn_gps.text = resources.getText(R.string.gps_status_disabled)
        }
    }

}