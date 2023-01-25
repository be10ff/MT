package com.example.mt.model.mapper

import com.example.mt.map.layer.XMLLayer
import com.example.mt.map.wkt.WktLayer

class LayerMapper {
    fun mapFrom(layer: XMLLayer): WktLayer {
        return WktLayer(layer.geometries)
    }
}