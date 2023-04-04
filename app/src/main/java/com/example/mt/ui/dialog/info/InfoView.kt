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
import com.example.mt.databinding.DialogInfoBinding
import com.example.mt.functional.updateFlow
import com.example.mt.map.wkt.DBaseField
import com.example.mt.map.wkt.WktGeometry
import com.example.mt.map.wkt.WktPoint
import com.example.mt.model.gi.GILonLat
import com.example.mt.model.gi.LonLatFormat
import com.example.mt.model.gi.Projection
import com.example.mt.ui.view.onChange
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlin.coroutines.CoroutineContext

class InfoView(context: Context?, attrs: AttributeSet?) : LinearLayout(context, attrs) {

    private var _binding: DialogInfoBinding? = null
    val binding get() = _binding!!

    var geometry: WktGeometry? = null
    var callback: GeometryInfoCallback? = null

    val lon_dHolder = LonLatFormat.DD_dddd(0.0)
    val lon_dmHolder = LonLatFormat.DD_MMmm(0.0)
    val lon_dmsHolder = LonLatFormat.DD_MM_SSss(0.0)
    val lat_dHolder = LonLatFormat.DD_dddd(0.0)
    val lat_dmHolder = LonLatFormat.DD_MMmm(0.0)
    val lat_dmsHolder = LonLatFormat.DD_MM_SSss(0.0)

    val job = Job()
    val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job //Specifies the main thread plus a job

    val scope = CoroutineScope(coroutineContext)

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

    private val currentPosition : MutableStateFlow<GILonLat> = MutableStateFlow<GILonLat>(GILonLat(0.0, 0.0, Projection.WGS84))

    private val adapter : AttributesAdapter = AttributesAdapter(attributesCallback)

    private val updateLon : (Number) -> Unit = { result ->
        currentPosition.updateFlow(scope){
            it.copy(lon = result.toDouble())
        }
    }

    private val updateLat : (Number) -> Unit = { result ->
        currentPosition.updateFlow(scope){
            it.copy(lat = result.toDouble())
        }
    }

    init {
        val inflater = context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        _binding = DialogInfoBinding.inflate(inflater, this, true)
            binding.root
            .apply{
                binding.btnClose.setOnClickListener {
                        geometry?.selected = false
                        callback?.onClose(geometry)
                        this@InfoView.visibility = View.GONE
                    }

                binding.btnDelete.setOnClickListener {
                        callback?.onDelete(geometry)
                        this@InfoView.visibility = View.GONE
                    }

                binding.btnEdit.setOnClickListener {
                        callback?.onEdit(geometry)
                    }


                binding.btnpOI.setOnClickListener {
                        geometry?.let {
                            geometry?.let{
                                it.marker = !it.marker
                            }
                            callback?.onSetPoi(geometry)
                        }
                    }

                binding.btnAdd.setOnClickListener {
                            callback?.onAdd()
                        }

                binding.etLonDDdddd.onChanged (lon_dHolder::setDegrees, updateLon)
                binding.etLatDDdddd.onChanged (lat_dHolder::setDegrees, updateLat)
                binding.etLonDDMMmm.onChanged (lon_dmHolder::setDegrees, updateLon)
                binding.etLatDDMMmm.onChanged (lat_dmHolder::setDegrees, updateLat)
                binding.etLonMMmm.onChanged (lon_dmHolder::setMinutes, updateLon)
                binding.etLatMMmm.onChanged (lat_dmHolder::setMinutes, updateLat)
                binding.etLonDDmmss.onChanged (lon_dmsHolder::setDegrees, updateLon)
                binding.etLonMMss.onChanged (lon_dmsHolder::setMinutes, updateLon)
                binding.etLonSS.onChanged (lon_dmsHolder::setSeconds, updateLon)
                binding.etLatDDmmss .onChanged (lat_dmsHolder::setDegrees, updateLat)
                binding.etLatMMss.onChanged (lat_dmsHolder::setMinutes, updateLat)
                binding.etLatSS.onChanged (lat_dmsHolder::setSeconds, updateLat)

                adapter.attributes = geometry?.attributes?.values?.toMutableList() ?: mutableListOf()
                binding.rvAttrs.layoutManager = LinearLayoutManager(context)
                binding.rvAttrs.adapter = adapter

                currentPosition.onEach {
                    lat_dHolder.internal = it.lat
                    lon_dHolder.internal = it.lon
                    lat_dmHolder.internal = it.lat
                    lon_dmHolder.internal = it.lon
                    lat_dmsHolder.internal = it.lat
                    lon_dmsHolder.internal = it.lon

                    binding.etLonDDdddd.setText(lon_dHolder.getDegrees().toString())
                    binding.etLatDDdddd.setText(lat_dHolder.getDegrees().toString())

                    binding.etLonDDMMmm.setText(lon_dmHolder.getDegrees().toString())
                    binding.etLonMMmm.setText(lon_dmHolder.getMinutes().toString())
                    binding.etLatDDMMmm.setText(lat_dmHolder.getDegrees().toString())
                    binding.etLatMMmm.setText(lat_dmHolder.getMinutes().toString())

                    binding.etLonDDmmss.setText(lon_dmsHolder.getDegrees().toString())
                    binding.etLatDDmmss.setText(lat_dmsHolder.getDegrees().toString())

                    binding.etLonMMss.setText(lon_dmsHolder.getMinutes().toString())
                    binding.etLatMMss.setText(lat_dmsHolder.getMinutes().toString())

                    binding.etLonSS.setText(lon_dmsHolder.getSeconds().toString())
                    binding.etLatSS.setText(lat_dmsHolder.getSeconds().toString())

//                    (geometry as? WktPoint)?.let { point ->
//                        point.point = it
//                        callback?.onUpdate()
//                    }

                }.launchIn(scope)
            }
    }

    fun consume(geometry: WktGeometry?) {
        this@InfoView.geometry = geometry
        (geometry as? WktPoint)?.let{ point ->
//            this@InfoView.geometry = it
//            this@InfoView.parent.visibility = View.VISIBLE
            this@InfoView.visibility = View.VISIBLE
            binding.llDegrees.visibility = View.VISIBLE
            binding.llMinutes.visibility = View.VISIBLE
            binding.llSeconds.visibility = View.VISIBLE
            (geometry as? WktPoint)?.point?.let{
                scope.launch {
                    currentPosition.updateFlow(this){lonLat ->
                        point.point
                    }
                }
            } ?: run{
                binding.llDegrees.visibility = View.GONE
                binding.llMinutes.visibility = View.GONE
                binding.llSeconds.visibility = View.GONE
            }
//            currentPosition.updateFlow(scope){lonLat ->
//                point.point
//            }

            adapter.attributes = point.attributes.values.toMutableList()
            adapter.notifyDataSetChanged()
        } ?: run {
            this@InfoView.visibility = View.GONE
        }
    }

    private fun EditText.onChanged(transform : (Number)-> Number, action: (Number) -> Unit) {
        setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) editableText?.toString()?.toDoubleOrNull()?.let { value ->
                textSize = context.resources.getDimension(R.dimen.tiny_text_size)
                val result = transform(value).toDouble()
                action(result)
            } else {
                textSize = context.resources.getDimension(R.dimen.small_text_size)
            }
        }
    }


}