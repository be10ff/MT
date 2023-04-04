//package com.example.mt.ui.dialog.info
//
//import android.content.Context
//import android.content.Context.LAYOUT_INFLATER_SERVICE
//import android.view.LayoutInflater
//import android.view.View
//import android.widget.ImageView
//import android.widget.LinearLayout
//import android.widget.PopupWindow
//import android.widget.TextView
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import com.example.mt.R
//import com.example.mt.map.wkt.DBaseField
//import com.example.mt.map.wkt.WktGeometry
//import com.example.mt.map.wkt.WktPoint
//import kotlinx.android.synthetic.main.dialog_info.view.*
//@Deprecated("delete")
//class InfoPopup(val context: Context, geometry: WktGeometry, callback: GeometryCallback) : PopupWindow(context) {
//
//    val close : ImageView
//    val delete : ImageView
//    val edit : ImageView
//    val poi : ImageView
//
//    private val attributesCallback = object :AttributesCallback{
//        override fun onNameChanged(holder: RecyclerView.ViewHolder, value: String) {
//            val origin = adapter.attributes[holder.adapterPosition]
//            geometry.attributes.remove(origin.name)
//            geometry.attributes[value] = DBaseField(value, origin.value)
//        }
//
//        override fun onValueChanged(holder: RecyclerView.ViewHolder, value: String) {
//            val origin = adapter.attributes[holder.adapterPosition]
//            geometry.attributes[origin.name] = DBaseField(origin.name, value)
//        }
//
//    }
//
//    private val adapter : AttributesAdapter = AttributesAdapter(attributesCallback)
//
//    init{
//        val inflater = context.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
//        contentView = inflater.inflate(R.layout.dialog_info, null)
//            .apply{
//                close = findViewById<ImageView?>(R.id.btnClose).apply {
//                    setOnClickListener {
//                        geometry.selected = false
//                        callback.onClose()
//                        this@InfoPopup.dismiss()
//                    }
//                }
//
//                delete = findViewById<ImageView?>(R.id.btnDelete).apply {
//                    setOnClickListener {
//                        callback.onDelete()
//                        this@InfoPopup.dismiss()
//                    }
//                }
//
//                edit = findViewById<ImageView?>(R.id.btnEdit).apply {
//                    setOnClickListener {
//                        callback.onEdit()
//                    }
//                }
//
//                poi = findViewById<ImageView?>(R.id.btnpOI).apply {
//                    setOnClickListener {
//                        geometry.marker = !geometry.marker
//                        callback.onSetPoi()
//                    }
//                }
//
//
//                adapter.attributes = geometry.attributes.values.toMutableList()
//                rvAttrs.layoutManager = LinearLayoutManager(context)
//                rvAttrs.adapter = adapter
//            }
//        width = LinearLayout.LayoutParams.MATCH_PARENT;
//
//    }
//}