package com.example.mt.ui.view

import android.content.Context
import android.location.Location
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.mt.R
import com.example.mt.model.gi.Project
import kotlinx.android.synthetic.main.control_scale.view.*
import kotlin.math.floor
import kotlin.math.roundToInt

class ScaleControl @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attributeSet, defStyleAttr), IControl {

    val text: TextView
    val lon: TextView
    val lat: TextView
    val size = context.dp2Px(resources.getDimension(R.dimen.scaleMaxWidth).toInt())

    companion object {
        val nominals = listOf(
            20,
            50,
            100,
            250,
            500,
            1000,
            2000,
            3000,
            5000,
            10000,
            20000,
            50000,
            100000,
            500000,
            1000000,
            5000000,
            10000000
        )
    }

    init {
        (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(
            R.layout.control_scale,
            this
        ).also { view ->
            text = view.findViewById(R.id.tvScale)
            lon = view.findViewById(R.id.tvLon)
            lat = view.findViewById(R.id.tvLat)
        }
    }

    fun coordString(coord: Double): String {
        val dergees = floor(coord).toInt()
        val mins = floor((coord - dergees) * 60).toInt()
        val secs = ((coord - dergees) * 60 - mins) * 60
        return String.format("%d° %d\' %.4f\"", dergees, mins, secs)
    }

    override val gpsConsumer: (Location?, Project) -> Unit = { location, project ->
        location?.let {
            tvLon.text = coordString(it.longitude)
            tvLat.text = coordString(it.latitude)
        }

        nominals.lastOrNull { cur ->
            cur < size * project.metersInPixel
        }?.also { nearest ->
            text.layoutParams.width = (nearest / project.metersInPixel).roundToInt()
            tvScale.text = if (nearest < 1000) "$nearest m" else "${nearest / 1000} km"
        }

    }
}