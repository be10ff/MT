package com.example.mt.ui.dialog.settings

import android.view.View
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import com.example.mt.R

class ProjectHolder(val v: View, callback: LayerHolderCallback) : RecyclerView.ViewHolder(v) {
    val projectPath: EditText
    val projectName: EditText
    val description: EditText

    init {
        projectPath = v.findViewById(R.id.etProjectPath)
        projectName = v.findViewById(R.id.etProjectName)
        description = v.findViewById(R.id.etProjectDescription)
    }
}