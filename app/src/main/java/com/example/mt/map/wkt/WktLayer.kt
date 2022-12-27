package com.example.mt.map.wkt

import org.simpleframework.xml.Root
import org.simpleframework.xml.convert.Convert

@Root(name = "Geometries", strict = false)
@Convert(WktConverter::class)
data class WktLayer(
    val geometry: List<WktGeometry> = emptyList()
)