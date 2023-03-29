package com.example.mt.ui.dialog.settings

import android.view.View
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import com.example.mt.R
import com.example.mt.ui.dialog.IHolder
import com.example.mt.ui.view.onChange

class ProjectHolder(v: View) : RecyclerView.ViewHolder(v),
    IHolder {
    val projectPath: EditText
    val projectName: EditText
    val description: EditText

    init {
        projectPath = v.findViewById(R.id.etProjectPath)
        projectName = v.findViewById(R.id.etProjectName)
        description = v.findViewById(R.id.etProjectDescription)
    }

    override fun bind(callback: LayerHolderCallback, dragListener: OnStartDragListener) {
        projectName.onChange { callback.onProjectName(it) }
        projectPath.onChange { callback.onProjectPath(it) }
        description.onChange { callback.onProjectDescription(it) }
    }


    override fun unBind() {
        projectName.onFocusChangeListener = null
        projectPath.onFocusChangeListener = null
        description.onFocusChangeListener = null
    }
}