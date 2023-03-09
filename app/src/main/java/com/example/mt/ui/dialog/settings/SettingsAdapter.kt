package com.example.mt.ui.dialog.settings

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.mt.R
import com.example.mt.map.MapUtils
import com.example.mt.map.layer.Layer
import com.example.mt.map.layer.SQLLayer
import com.example.mt.model.gi.Project
import com.example.mt.model.xml.GILayerType
import com.example.mt.model.xml.SqlProjection
import java.io.File

class SettingsAdapter(
    private val callback: LayerHolderCallback,
    private val dragListener: OnStartDragListener
) : RecyclerView.Adapter<ViewHolder>(), ItemTouchHelperAdapter {

    private val layers: MutableList<Layer?> = mutableListOf(null)
    private var project: Project? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (viewType) {
            TYPE_SQL -> SQLLayerHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_sqllayer_list, parent, false), callback, dragListener
            )
            TYPE_DEFAULT -> LayerHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_layer_list, parent, false), callback, dragListener
            )
            TYPE_GROUP -> ProjectHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_layer_project, parent, false), callback
            )
            else -> LayerHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_layer_list, parent, false), callback, dragListener
            )
        }

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (getItemViewType(position) == TYPE_GROUP) {
            with(holder as ProjectHolder) {
                projectName.setText(project?.name)
                projectPath.setText(project?.saveAs)
                description.setText(project?.description)
            }
        } else {
            with(holder as LayerHolder) {
                layers[position]?.let { layer ->
                    filePath.text = layer.name
                    enabled.isChecked = layer.enabled
                    exist.setImageResource(if (File(layer.source).exists()) R.drawable.source_exist else R.drawable.no_source)
                    val min = MapUtils.scale2z(layer.rangeTo ?: 0)
                    val max = MapUtils.scale2z(layer.rangeFrom ?: 0)
                    range.selectedMinValue = (min.toFloat())
                    range.selectedMaxValue = (max.toFloat())
                    rangeMin.text = min.toString()
                    rangeMax.text = max.toString()
                }
            }
            (holder as? SQLLayerHolder)?.let { sqlHolder ->
                (layers[position] as? SQLLayer)?.let { sqlLayer ->
                    sqlHolder.tileProjection.isChecked =
                        sqlLayer.sqlProjection == SqlProjection.GOOGLE
                }
            }
        }
    }

    override fun getItemCount(): Int = layers.size

    override fun getItemViewType(position: Int): Int {
        return when {
            layers[position] == null -> TYPE_GROUP
            layers[position]?.type == GILayerType.XML -> TYPE_XML
            layers[position]?.type in listOf(GILayerType.SQL) -> TYPE_SQL
            else -> TYPE_DEFAULT
        }
    }

    fun setData(proj: Project) {
        project = proj
        layers.apply {
            clear()
            add(null)
            addAll(proj.layers)
        }
    }

    fun getLayer(holder: ViewHolder): Layer? = layers[holder.adapterPosition]

    override fun onItemMove(from: ViewHolder, to: ViewHolder): Boolean {
        callback.onMove(from, to)
        return true
    }

    override fun onItemDismiss(holder: ViewHolder) {
        callback.onRemove(holder)
    }

    companion object {
        const val TYPE_DEFAULT = 2
        const val TYPE_GROUP = 1
        const val TYPE_SQL = 3
        const val TYPE_XML = 4
    }


}