package com.example.mt.ui.dialog.settings

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mt.MainActivity
import com.example.mt.R
import com.example.mt.databinding.DialogSettingsBinding
import com.example.mt.map.MapUtils
import com.example.mt.map.layer.XMLLayer
import com.example.mt.model.Action
import com.example.mt.model.xml.EditableType
import com.example.mt.model.xml.SqlProjection
import com.example.mt.ui.dialog.AbstractDialog
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class SettingsDialog : AbstractDialog(/*R.layout.dialog_settings*/), OnStartDragListener {

    var _binding: DialogSettingsBinding? = null
    val binding get() = _binding!!

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

        override fun onMarkersSource(holder: RecyclerView.ViewHolder) {
            (adapter.getLayer(holder) as? XMLLayer)
                ?.takeIf{ EditableType.POI == it.editableType}
                ?.let{ layer ->
                fragmentViewModel.submitAction(Action.ProjectAction.MarkersSourceSelected(layer))
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.FullScreenDialgStyle)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun setupObserve() {
        lifecycleScope.launch {
            fragmentViewModel.projectState
                .collect { project ->
                    (binding.rvLayers.adapter as? SettingsAdapter)?.let {
                        it.setData(project)
                        binding.rvLayers.post {
                            it.notifyDataSetChanged()
                        }
                    }
                }
        }
    }

    override fun setupGUI(savedInstanceState: Bundle?) {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        binding.rvLayers.layoutManager = LinearLayoutManager(requireContext())
        binding.rvLayers.adapter = adapter
        helper.attachToRecyclerView(binding.rvLayers)

        binding.fabAddLayer.setOnClickListener {
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