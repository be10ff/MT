package com.example.mt.ui.main

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Point
import android.graphics.Rect
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.map
import com.example.mt.R
import com.example.mt.map.layer.GILayer
import com.example.mt.model.Action
import com.example.mt.model.gi.GIBounds
import com.example.mt.model.gi.GIProjection
import com.example.mt.ui.view.ControlListener
import kotlinx.android.synthetic.main.main_fragment.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

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
        mainViewModel.mainState.map {
            it.mapState
        }
            .distinctUntilChanged()
            .observe(viewLifecycleOwner) { mapState ->
                Log.d("THREAD", "observed" + this)

            }
        mainViewModel.bitmapState
//            .distinctUntilChanged()
            .observe(viewLifecycleOwner) {
                Log.d("TOUCH", "draw" + this)
                map.reDraw(it)
            }

        mainViewModel.mainState.map {
            it.mapState
        }
            .distinctUntilChanged()
            .observe(viewLifecycleOwner) { mapState ->
                runBlocking {
                    launch(Dispatchers.Default) {
                        map.bitmap
                            ?.apply {
                                eraseColor(Color.WHITE)
                                Canvas(this).let { canvas ->
                                    GILayer.sqlTest.redraw(mapState.bounds, this, 0, 0.0)
                                    canvas.drawBitmap(
                                        this,
                                        mapState.viewRect,
                                        mapState.viewRect,
                                        null
                                    )
                                }
                            }
                    }
                }

            }

    }

    private fun setupGUI() {
        control.listener = this
        mainViewModel.submitAction(
            Action.MapAction.BoundsChanged(
                GIBounds(
                    GIProjection.WGS84,
                    28.0,
                    65.0,
                    48.0,
                    46.0
                ).reproject(GIProjection.WorldMercator)
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
            mainViewModel.submitAction(Action.MapAction.InitMapView(map.bitmap))
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

    }

//    override fun invalidate() {
////        map.invalidate()
//    }

    override fun updateMap() {
        mainViewModel.submitAction(Action.MapAction.Update)
    }

    override fun boundsChanged(bounds: GIBounds) {

    }

    override fun viewRectChanged(rect: Rect) {
        mainViewModel.submitAction(Action.MapAction.ViewRectChanged(rect))
    }
}