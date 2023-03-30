package com.example.mt.ui.dialog.info

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mt.R
import com.example.mt.map.wkt.DBaseField
import com.example.mt.map.wkt.WktGeometry
import com.example.mt.map.wkt.WktPoint
import com.example.mt.ui.view.onChange
import kotlinx.android.synthetic.main.dialog_info.view.*

class InfoView(context: Context?, attrs: AttributeSet?) : LinearLayout(context, attrs) {

    val close : ImageView
    val delete : ImageView
    val edit : ImageView
    val poi : ImageView
    val lonLat: TextView

    val lonDDdddd: EditText
    val latDDdddd: EditText

    val lonDDMMmm: EditText
    val lonMMmm: EditText
    val latDDMMmm: EditText
    val latMMmm: EditText

    val lonDDMMss: EditText
    val lonMMss: EditText
    val lonSS: EditText
    val latDDMMss: EditText
    val latMMss: EditText
    val latSS: EditText

    var geometry: WktGeometry? = null
    var callback: GeometryInfoCallback? = null

    private val attributesCallback = object :AttributesCallback{
        override fun onNameChanged(holder: RecyclerView.ViewHolder, value: String) {
            geometry?.let{
                val origin = adapter.attributes[holder.adapterPosition]
                geometry?.attributes?.remove(origin.name)
                geometry?.attributes?.set(value, DBaseField(value, origin.value))
            }

        }

        override fun onValueChanged(holder: RecyclerView.ViewHolder, value: String) {
            geometry?.let {
                val origin = adapter.attributes[holder.adapterPosition]
                geometry?.attributes?.set(origin.name, DBaseField(origin.name, value))
            }
        }

    }

    private val adapter : AttributesAdapter = AttributesAdapter(attributesCallback)

    init {
        val inflater = context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val v: View = inflater.inflate(R.layout.dialog_info, this)
            .apply{
                close = findViewById<ImageView?>(R.id.btnClose).apply {
                    setOnClickListener {
                        geometry?.selected = false
                        callback?.onClose(geometry)
                        this@InfoView.visibility = View.GONE
                    }
                }

                delete = findViewById<ImageView?>(R.id.btnDelete).apply {
                    setOnClickListener {
                        callback?.onDelete(geometry)
                        this@InfoView.visibility = View.GONE
                    }
                }

                edit = findViewById<ImageView?>(R.id.btnEdit).apply {
                    setOnClickListener {
                        callback?.onEdit(geometry)
                    }
                }

                poi = findViewById<ImageView?>(R.id.btnpOI).apply {
                    setOnClickListener {
                        geometry?.let {
                            geometry?.let{
                                it.marker = !it.marker
                            }
                            callback?.onSetPoi(geometry)
                        }
                    }
                }

                lonLat = findViewById<TextView?>(R.id.lonlat).apply {
                    text = (geometry as? WktPoint)?.point?.toString()
                }


                lonDDdddd = findViewById<EditText?>(R.id.etLonDDdddd).apply {
                    this.onChange {  }
                }
                latDDdddd = findViewById<EditText?>(R.id.etLatDDdddd).apply {
                    this.onChange {  }
                }

                lonDDMMmm = findViewById<EditText?>(R.id.etLonDDMMmm).apply {
                    this.onChange {  }
                }
                lonMMmm = findViewById<EditText?>(R.id.etLonMMmm).apply {
                    this.onChange {  }
                }
                latDDMMmm = findViewById<EditText?>(R.id.etLatnDDMMmm).apply {
                    this.onChange {  }
                }
                latMMmm = findViewById<EditText?>(R.id.etLatMMmm).apply {
                    this.onChange {  }
                }

                lonDDMMss = findViewById<EditText?>(R.id.etLonDDmmss).apply {
                    this.onChange {  }
                }
                lonMMss = findViewById<EditText?>(R.id.etLonMMss).apply {
                    this.onChange {  }
                }
                lonSS = findViewById<EditText?>(R.id.etLonSS).apply {
                    this.onChange {  }
                }
                latDDMMss = findViewById<EditText?>(R.id.etLatDDmmss).apply {
                    this.onChange {  }
                }
                latMMss = findViewById<EditText?>(R.id.etLatMMss).apply {
                    this.onChange {  }
                }
                latSS = findViewById<EditText?>(R.id.etLatSS).apply {
                    this.onChange {  }
                }

                adapter.attributes = geometry?.attributes?.values?.toMutableList() ?: mutableListOf()
                rvAttrs.layoutManager = LinearLayoutManager(context)
                rvAttrs.adapter = adapter
            }
    }

    fun consume(geometry: WktGeometry?) {
        this@InfoView.geometry = geometry
        geometry?.let{
//            this@InfoView.geometry = it
            this@InfoView.visibility = View.VISIBLE
            adapter.attributes = it.attributes.values.toMutableList()
            adapter.notifyDataSetChanged()
            lonLat.text = (geometry as? WktPoint)?.point?.toString()
            invalidate()
        } ?: run {
            this@InfoView.visibility = View.GONE
        }
    }

}