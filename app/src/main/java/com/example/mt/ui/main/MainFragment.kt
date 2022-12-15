package com.example.mt.ui.main

import android.graphics.Point
import android.graphics.Rect
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.observe
import com.example.mt.R
import com.example.mt.model.Action
import com.example.mt.model.gi.Bounds
import com.example.mt.model.gi.Projection
import com.example.mt.model.xml.GIBounds
import com.example.mt.ui.view.ControlListener
import kotlinx.android.synthetic.main.main_fragment.*
import kotlinx.coroutines.InternalCoroutinesApi

class MainFragment : Fragment(), ControlListener {

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

    @InternalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObserve()
        setupGUI()
    }

    @InternalCoroutinesApi
    private fun setupObserve() {

        mainViewModel.gpsState.observe(viewLifecycleOwner) { location ->

        }
        mainViewModel.gpsState.observe(viewLifecycleOwner, gpsObserver)

        mainViewModel.storageState.observe(viewLifecycleOwner) { granted ->

        }
//        mainViewModel.mainState.map {
//            it.mapState
//        }
//            .distinctUntilChanged()
//            .observeForever { mapState ->
//                Log.d("THREAD", "observed" + this)
//
//            }
        mainViewModel.bitmapState
            .distinctUntilChanged()
            .observeForever {
                map.reDraw(it)
//                ivMap.setImageBitmap(it)
            }

        mainViewModel.projectState
            .distinctUntilChanged()
            .observeForever {
//                mainViewModel.saveProject(it)
            }
    }

    private fun setupGUI() {
        control.listener = this
        mainViewModel.submitAction(Action.ProjectAction.Load(""))

        mainViewModel.submitAction(
            Action.MapAction.BoundsChanged(
                Bounds(
                    Projection.WGS84,
                    33.0,
                    57.0,
                    37.0,
                    53.0
                ).reproject(Projection.WorldMercator)
            )
        )

        map.addOnLayoutChangeListener { v, left, top, right, bottom, _, _, _, _ ->
            mainViewModel.submitAction(
                Action.MapAction.InitViewRect(
                    Rect(
                        left,
                        top,
                        right,
                        bottom
                    )
                )
            )
//            mainViewModel.submitAction(Action.MapAction.InitMapView(map.bitmap))
        }


    }

    private val gpsObserver: Observer<Location?> = Observer { location ->
        location?.let { location ->
//            btn_gps.text = location.toString()
        } ?: run {
//            btn_gps.text = resources.getText(R.string.gps_status_disabled)
        }
    }

    override fun onSetToDraft(b: Boolean) {

    }

    override fun moveViewBy(x: Int, y: Int) {
        mainViewModel.submitAction(Action.MapAction.MoveViewBy(x, y))
    }

    override fun scaleViewBy(focus: Point, scaleFactor: Float) {
        mainViewModel.submitAction(Action.MapAction.ScaleMapBy(focus, scaleFactor))
    }

    override fun updateMap() {
        mainViewModel.submitAction(Action.MapAction.Update)
    }

    override fun boundsChanged(bounds: GIBounds) {

    }

    override fun viewRectChanged(rect: Rect) {
        mainViewModel.submitAction(Action.MapAction.ViewRectChanged(rect))
    }
}