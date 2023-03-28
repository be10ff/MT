package com.example.mt.ui.view

import android.location.Location
import com.example.mt.map.wkt.WktPoint
import com.example.mt.model.ControlState
import com.example.mt.model.SensorState
import com.example.mt.model.gi.Project

interface IControl {
    fun consume(state: ControlState)
}