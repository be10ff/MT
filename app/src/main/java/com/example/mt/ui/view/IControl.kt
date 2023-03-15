package com.example.mt.ui.view

import android.location.Location
import com.example.mt.model.gi.Project

interface IControl {
    //    val collector: (Project) -> Unit
    val gpsConsumer: (Location?, Project) -> Unit
}