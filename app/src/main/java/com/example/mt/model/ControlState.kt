package com.example.mt.model

import com.example.mt.map.wkt.WktPoint
import com.example.mt.model.gi.Project

data class ControlState(
    val sensorState: SensorState,
    val project: Project,
    val selection: WktPoint?
)

