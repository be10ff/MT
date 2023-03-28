package com.example.mt.ui.main

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Point
import android.graphics.Rect
import android.os.Bundle
import android.os.Environment
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.mt.MainActivity
import com.example.mt.R
import com.example.mt.map.wkt.WktPoint
import com.example.mt.model.Action
import com.example.mt.model.BitmapState
import com.example.mt.model.Status
import com.example.mt.model.TrackState
import com.example.mt.ui.dialog.info.GeometryCallback
import com.example.mt.ui.dialog.info.InfoPopup
import com.example.mt.ui.dialog.markers.MarkersDialog
import com.example.mt.ui.dialog.settings.SettingsDialog
import com.example.mt.ui.view.ControlListener
import com.example.mt.ui.view.PositionControl
import kotlinx.android.synthetic.main.main_fragment.*
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File

class MainFragment : Fragment(), ControlListener {

    companion object {
        fun newInstance() = MainFragment()
    }

    private val fragmentViewModel: FragmentViewModel by activityViewModels()
    private var positionControl: PositionControl? = null
    private var infoPopUp: InfoPopup? = null

    private val startForResultProject =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let { uri ->
                    MainActivity.getRealPath(context, uri)
                }?.let { fileName ->
                    if (File(fileName).extension == "pro") {
                        fragmentViewModel.submitAction(Action.ProjectAction.Load(fileName))
                        activity?.let {
                            it.getPreferences(Context.MODE_PRIVATE).edit().run {
                                val key = it.resources.getString(R.string.key_last_project)
                                putString(key, fileName)
                            }
                        }
                    }
                }
            }
        }

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
        positionControl = PositionControl(requireContext())
            .also{
                main.addView(it)
            }
    }

    @InternalCoroutinesApi
    private fun setupObserve() {

        lifecycleScope.launch {
            fragmentViewModel.permissionState
                .filter { it == Status.Granted }
                .collectLatest {
                    val defaultPath =
                        "${Environment.getExternalStorageDirectory().absolutePath}/DefaultProject.pro"
                    val lastName = activity?.let {
                        val key = it.resources.getString(R.string.key_last_project)
                        it.getPreferences(Context.MODE_PRIVATE)?.getString(key, defaultPath)
                    } ?: defaultPath
                    fragmentViewModel.submitAction(Action.PermissionAction.ManageFileGranted(Status.Consumed))
                    fragmentViewModel.submitAction(Action.ProjectAction.Load(lastName))
                }
        }

        lifecycleScope.launch {
            fragmentViewModel.bitmapState
                .filterIsInstance<BitmapState.Defined>()
                .onEach {
                    map.reDraw(it.bitmap)
                }.collect()
        }

        fragmentViewModel.selectedGeometryState
            .onEach {  geometry ->
                geometry?.let {
                    if (geometry.selected)
                        when {
                            else -> {
                                activity?.let {
                                    InfoPopup(it, geometry, object : GeometryCallback {

                                        override fun onEdit() {
                                            (geometry as? WktPoint)?.let{
                                                fragmentViewModel.submitAction(
                                                    Action.GeometryAction.Edit(
                                                        geometry
                                                    )
                                                )
                                            }
                                        }

                                        override fun onDelete() {
                                            fragmentViewModel.submitAction(
                                                Action.GeometryAction.Delete(
                                                    geometry
                                                )
                                            )
                                        }

                                        override fun onSetPoi() {
                                            (geometry as? WktPoint)?.let{
                                            fragmentViewModel.submitAction(
                                                Action.GeometryAction.SetPoi(
                                                    geometry
                                                    )
                                                )

                                            }
                                        }

                                        override fun onClose() {
                                            (geometry as? WktPoint)?.let{
                                                fragmentViewModel.submitAction(
                                                    Action.GeometryAction.ChangeSelected(
                                                        geometry
                                                    )
                                                )

                                            }
                                        }
                                    })
                                        .also{
                                            infoPopUp = it
                                        }
                                        .showAtLocation(main, Gravity.TOP, 0, 0)
                                }
                            }
                        }
                } ?: run { /*infoPopUp?.dismiss()*/ }
            }
            .launchIn(lifecycleScope)

        fragmentViewModel.buttonState
            .map { it.writeTrack }
            .onEach { state ->
                btnWriteTrack.setImageResource(if (state is TrackState.Started) R.drawable.ic_stop_track else R.drawable.ic_start_track)
            }
            .launchIn(lifecycleScope)

        fragmentViewModel.buttonState
            .map { it.follow }
            .onEach { state ->
                btnFollow.setImageResource(if (state) R.drawable.ic_follow_diableled else R.drawable.ic_follow)
            }
            .launchIn(lifecycleScope)

        fragmentViewModel.buttonState
            .map { it.editGeometry }
            .onEach { state ->
                infoPopUp?.edit?.setImageResource(if (state) R.drawable.ic_close else R.drawable.ic_edit)
            }
            .launchIn(lifecycleScope)
        fragmentViewModel.selectedGeometryState
            .onEach {
                (it as? WktPoint)?.let{
                    infoPopUp?.lonLat?.text = it.point.toString()
                }
            }.launchIn(lifecycleScope)

        lifecycleScope.launch {
            fragmentViewModel.controlState
                .collectLatest { state ->
                    positionControl?.consume(state)
                    control_scale.consume(state)
//                    cDirection.consume(state)
//                    cCompass.consume(state)
                }
        }
    }

    private fun setupGUI() {
        control.listener = this
        map.addOnLayoutChangeListener { _, left, top, right, bottom, _, _, _, _ ->
            fragmentViewModel.submitAction(
                Action.MapAction.ViewRectChanged(
                    Rect(
                        left,
                        top,
                        right,
                        bottom
                    )
                )
            )
        }


        setupButtons()
    }

    override fun moveViewBy(x: Int, y: Int) {
        fragmentViewModel.submitAction(Action.MapAction.MoveViewBy(x, y))
    }

    override fun scaleViewBy(focus: Point, scaleFactor: Float) {
        fragmentViewModel.submitAction(Action.MapAction.ScaleMapBy(focus, scaleFactor))
    }

    override fun updateMap() {
        fragmentViewModel.submitAction(Action.MapAction.Update)
    }

    override fun clickAt(point: Point) {
        fragmentViewModel.submitAction(Action.MapAction.ClickAt(point))
    }

    private fun setupButtons() {

        //fabGps
        btnWriteTrack.setOnClickListener {
            fragmentViewModel.submitAction(Action.ButtonAction.WriteTrack)
        }

        btnFollow.setOnClickListener {
            fragmentViewModel.submitAction(Action.ButtonAction.FollowPosition)
        }


        btnAddPoi.setOnClickListener {
            fragmentViewModel.submitAction(Action.ButtonAction.AddPosition)
        }

        btnOptions.setOnClickListener {
            SettingsDialog().show(childFragmentManager, SettingsDialog.TAG)
        }

        btnOpenProject.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
                .apply {
                    type = "file/*"
                }
            startForResultProject.launch(intent)

        }

        btnMarkers.setOnClickListener {
            MarkersDialog().show(childFragmentManager, MarkersDialog.TAG)
        }

    }

}