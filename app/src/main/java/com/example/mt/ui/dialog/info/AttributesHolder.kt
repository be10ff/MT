package com.example.mt.ui.dialog.info

import android.view.View
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import com.example.mt.R
import com.example.mt.ui.dialog.IHolder
import com.example.mt.ui.view.onChange

class AttributesHolder(view: View, callback: AttributesCallback) : RecyclerView.ViewHolder(view),
    IHolder {

    val value: EditText
    val name: EditText

    init{
        name = view.findViewById(R.id.etName)
        value = view.findViewById(R.id.etValue)

        name.onChange { callback.onNameChanged(this, it) }
        value.onChange { callback.onValueChanged(this, it) }
    }

    override fun unBind() {
        name.onFocusChangeListener = null
        value.onFocusChangeListener = null
    }

}
