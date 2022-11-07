package com.example.mt.model.properties

import com.example.mt.map.GILayerType

data class LayerProperties(
    //ToDo refactor to sealed
    val name: String,
    val type: GILayerType,
    val enabled: Boolean,
    val sqldbProperties: SQLDBProperties
)
