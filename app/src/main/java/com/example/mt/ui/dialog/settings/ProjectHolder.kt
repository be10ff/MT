package com.example.mt.ui.dialog.settings

import android.view.View
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import com.example.mt.R
import com.example.mt.ui.dialog.IHolder

class ProjectHolder(val v: View, callback: LayerHolderCallback) : RecyclerView.ViewHolder(v),
    IHolder {
    val projectPath: EditText
    val projectName: EditText
    val description: EditText

    init {
        projectPath = v.findViewById(R.id.etProjectPath)
        projectName = v.findViewById(R.id.etProjectName)
        description = v.findViewById(R.id.etProjectDescription)

        projectName.onChange { callback.onProjectName(it) }
        projectPath.onChange { callback.onProjectPath(it) }
        description.onChange { callback.onProjectDescription(it) }
    }

    fun EditText.onChange(action: (String) -> Unit) {
        setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) editableText?.let { action(it.toString()) }
        }
    }

    override fun unBind() {
        projectName.onFocusChangeListener = null
        projectPath.onFocusChangeListener = null
        description.onFocusChangeListener = null
    }
}