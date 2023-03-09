package com.example.mt.ui.main

import android.app.Activity
import android.content.Intent
import android.graphics.Point
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.mt.MainActivity
import com.example.mt.R
import com.example.mt.functional.launchWhenStarted
import com.example.mt.model.Action
import com.example.mt.model.BitmapState
import com.example.mt.model.TrackState
import com.example.mt.model.xml.GIBounds
import com.example.mt.ui.dialog.settings.SettingsDialog
import com.example.mt.ui.view.ControlListener
import kotlinx.android.synthetic.main.main_fragment.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.io.File

class MainFragment : Fragment(), ControlListener {

    companion object {
        fun newInstance() = MainFragment()
    }


    private val fragmentViewModel: FragmentViewModel by activityViewModels()

    private val startForResultProject =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let { uri ->
                    MainActivity.getRealPath(context, uri)
                }?.let { fileName ->
                    if (File(fileName).extension == "pro")
                        fragmentViewModel.submitAction(Action.ProjectAction.Load(fileName))
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onStart() {
        super.onStart()
        CoroutineScope(Dispatchers.Main.immediate).launch {

        }
    }

    @InternalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObserve()
        setupGUI()
    }


    @InternalCoroutinesApi
    private fun setupObserve() {
        fragmentViewModel.bitmapState
            .filterIsInstance<BitmapState.Defined>()
            .onEach { map.reDraw(it.bitmap) }
            .launchWhenStarted(lifecycleScope)

        fragmentViewModel.buttonState
            .map { it.writeTrack }
            .distinctUntilChanged()
            .onEach { state ->
                if (state is TrackState.Started) {
                    btnWriteTrack.setImageResource(R.drawable.ic_stop_track)
                } else {
                    btnWriteTrack.setImageResource(R.drawable.ic_start_track)
                }
            }

        fragmentViewModel.buttonState
            .map { it.follow }
            .distinctUntilChanged()
            .onEach { state ->
                if (state) btnFollow.setImageResource(R.drawable.ic_follow_diableled) else btnFollow.setImageResource(
                    R.drawable.ic_follow
                )
            }

        fragmentViewModel.buttonState
            .map { it.editGeometry }
            .distinctUntilChanged()
            .onEach { state ->
                if (state) btnEdit.setImageResource(R.drawable.ic_close) else btnEdit.setImageResource(
                    R.drawable.ic_edit
                )
            }

        fragmentViewModel.buttonState
            .map { it.deleteGeometry }
            .distinctUntilChanged()
            .onEach { state ->
                if (state) btnDelete.setImageResource(R.drawable.ic_close) else btnDelete.setImageResource(
                    R.drawable.ic_delete
                )
            }

        fragmentViewModel.buttonState
            .onEach { state ->
//                if (state.writeTrack is TrackState.Started)  {
//                    btnWriteTrack.setImageResource(R.drawable.ic_stop_track)
//                    requireActivity().let{
//                        val intent = Intent(it, TrackService::class.java)
//                        intent.putExtra(TrackService.FILENAME, state.writeTrack.fileName)
//                        startForegroundService(it, intent)
//                    }
//                } else {
//                    btnWriteTrack.setImageResource(R.drawable.ic_start_track)
//                    requireContext().let {
//                        val intent = Intent(it, TrackService::class.java)
//                        activity?.stopService(intent)
//                    }
//
//                }
//                if (state.follow) btnFollow.setImageResource(R.drawable.ic_follow_diableled) else btnFollow.setImageResource(
//                    R.drawable.ic_follow
//                )
//                if (state.editGeometry) btnEdit.setImageResource(R.drawable.ic_close) else btnEdit.setImageResource(
//                    R.drawable.ic_edit
//                )
//                if (state.deleteGeometry) btnDelete.setImageResource(R.drawable.ic_close) else btnDelete.setImageResource(
//                    R.drawable.ic_delete
//                )
            }
    }

    private fun setupGUI() {
        control.listener = this
        val viewTreeObserver = map.viewTreeObserver
        if (viewTreeObserver.isAlive) {
            viewTreeObserver.addOnGlobalLayoutListener {
                Rect(map.left, map.top, map.right, map.bottom)
                    .takeIf {
                        !it.isEmpty
                    }?.let {
                        fragmentViewModel.submitAction(Action.MapAction.ViewRectChanged(it))
                    }

            }
        }
        setupButtons()
    }

    override fun onSetToDraft(b: Boolean) {

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

    override fun boundsChanged(bounds: GIBounds) {

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