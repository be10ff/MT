package com.example.mt.ui.main

import android.graphics.Point
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.distinctUntilChanged
import com.example.mt.R
import com.example.mt.model.Action
import com.example.mt.model.gi.Bounds
import com.example.mt.model.gi.Projection
import com.example.mt.model.xml.GIBounds
import com.example.mt.ui.dialog.SettingsDialog
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

    override fun onPause() {
        super.onPause()
        activity
        mainViewModel.submitAction(Action.ProjectAction.Save)
    }

    @InternalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObserve()
        setupGUI()
    }

    @InternalCoroutinesApi
    private fun setupObserve() {

        mainViewModel.trackingState.observe(viewLifecycleOwner) { (location, buttonState) ->
            buttonState?.let { buttons ->
                location?.let { loc ->
                    when {
                        buttons.follow -> {

                        }
                        buttons.writeTrack -> {}
                    }
                }
            }
        }

        mainViewModel.bitmapState
            .distinctUntilChanged()
            .observeForever {
                map.reDraw(it)
            }

//        mainViewModel.projectState
//            .distinctUntilChanged()
//            .observeForever {
//                mainViewModel.reDraw()
//            }

        mainViewModel.buttonState
            .distinctUntilChanged()
            .observeForever { state ->
                if (state.writeTrack) btnWriteTrack.setImageResource(R.drawable.ic_stop_track) else btnWriteTrack.setImageResource(
                    R.drawable.ic_start_track
                )
                if (state.follow) btnFollow.setImageResource(R.drawable.ic_follow_diableled) else btnFollow.setImageResource(
                    R.drawable.ic_follow
                )
                if (state.editGeometry) btnEdit.setImageResource(R.drawable.ic_close) else btnEdit.setImageResource(
                    R.drawable.ic_edit
                )
                if (state.deleteGeometry) btnDelete.setImageResource(R.drawable.ic_close) else btnDelete.setImageResource(
                    R.drawable.ic_delete
                )
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

        setupButtons()
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

    private fun setupButtons() {
        fabGps.setOnClickListener {
            if (!eflGps.isOpen()) {
                eflEdit.close()
                eflCompass.close()
            }
        }

        fabCompass.setOnClickListener {
            if (!eflCompass.isOpen()) {
                eflEdit.close()
                eflGps.close()
            }
        }

        fabEdit.setOnClickListener {
            if (!eflEdit.isOpen()) {
                eflCompass.close()
                eflGps.close()
            }
        }

        //fabGps
        btnWriteTrack.setOnClickListener {
            mainViewModel.submitAction(Action.ButtonAction.WriteTrack)
        }

        btnFollow.setOnClickListener {
            mainViewModel.submitAction(Action.ButtonAction.FollowPosition)
        }


        btnAddPoi.setOnClickListener {
            mainViewModel.submitAction(Action.ButtonAction.AddPosition)
        }

        btnOptions.setOnClickListener {
            SettingsDialog().show(childFragmentManager, SettingsDialog.TAG)
        }

//        fabCompass
        btnMarkers
        btnOpenEdit
        btnOptions
        btnOpenProject

        //fabEdit
        btnEdit
        btnDelete
        btnAttributes
        btnAddPoints
        btnClose

    }
}