package com.example.mt.ui.dialog.settings

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mt.MainActivity
import com.example.mt.R
import com.example.mt.map.MapUtils
import com.example.mt.model.Action
import com.example.mt.model.xml.SqlProjection
import com.example.mt.ui.dialog.AbstractDialog
import kotlinx.android.synthetic.main.dialog_settings.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class SettingsDialog : AbstractDialog(R.layout.dialog_settings), OnStartDragListener {
    private val startForResultProject =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let { uri ->
                    MainActivity.getRealPath(context, uri)
                }?.let { fileName ->
                    fragmentViewModel.submitAction(Action.ProjectAction.AddLayer(fileName))
                }
            }
        }

    private val callback: LayerHolderCallback = object : LayerHolderCallback {
        override fun onVisibilityChanged(holder: RecyclerView.ViewHolder, isChecked: Boolean) {
            adapter.getLayer(holder)?.let { layer ->
                fragmentViewModel.submitAction(
                    Action.ProjectAction.VisibilityChanged(layer, isChecked)
                )
            }
        }

        override fun onRangeChanging(holder: RecyclerView.ViewHolder, min: Float, max: Float) {
            adapter.getLayer(holder)?.let { layer ->
                val minScale = MapUtils.z2scale(max.roundToInt())
                val maxScale = MapUtils.z2scale(min.roundToInt())
                fragmentViewModel.submitAction(
                    Action.ProjectAction.RangeChanged(
                        layer,
                        minScale,
                        maxScale
                    )
                )
            }
        }

        override fun onRemove(holder: RecyclerView.ViewHolder) {
            adapter.getLayer(holder)?.let { layer ->
                fragmentViewModel.submitAction(Action.ProjectAction.RemoveLayer(layer))
            }

        }

        override fun onMove(from: RecyclerView.ViewHolder, to: RecyclerView.ViewHolder) {
            adapter.getLayer(from)?.let { layerFrom ->
                adapter.getLayer(to)?.let { layerTo ->
                    fragmentViewModel.submitAction(
                        Action.ProjectAction.MoveLayer(
                            layerFrom,
                            layerTo
                        )
                    )
                }
            }

        }

        override fun onType(holder: RecyclerView.ViewHolder, type: SqlProjection) {
            adapter.getLayer(holder)?.let { layer ->
                fragmentViewModel.submitAction(Action.ProjectAction.TypeChanged(layer, type))
            }
        }

        override fun onProjectName(name: String) =
            fragmentViewModel.submitAction(Action.ProjectAction.NameChanged(name))

        override fun onProjectPath(path: String) =
            fragmentViewModel.submitAction(Action.ProjectAction.PathChanged(path))

        override fun onProjectDescription(description: String) =
            fragmentViewModel.submitAction(Action.ProjectAction.DescriptionChanged(description))
    }

    private val adapter = SettingsAdapter(callback = callback, dragListener = this)
    private val touchCallback = LayerTouchHelperCallback(adapter)
    private val helper = ItemTouchHelper(touchCallback)

    override fun setupObserve() {
        lifecycleScope.launch {
            fragmentViewModel.projectState
                .collect { project ->
                    (rvLayers.adapter as? SettingsAdapter)?.let {
                        it.setData(project)
                        rvLayers.post {
                            it.notifyDataSetChanged()
                        }
                    }
                }
        }
    }

    override fun setupGUI(savedInstanceState: Bundle?) {
        rvLayers.layoutManager = LinearLayoutManager(requireContext())
        rvLayers.adapter = adapter
        helper.attachToRecyclerView(rvLayers)

        fabAddLayer.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
                .apply {
                    type = "file/*"
                }
            startForResultProject.launch(intent)
        }
    }

    companion object {
        val TAG = "PROJECT_SETTINGS_DIALOG"
    }

    override fun onStartDrag(viewHolder: RecyclerView.ViewHolder) {
        helper.startDrag(viewHolder)
    }

    override fun onStartSwipe(viewHolder: RecyclerView.ViewHolder) {
        helper.startSwipe(viewHolder)
    }
}