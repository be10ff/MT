package com.example.mt.model.xml

import org.simpleframework.xml.Attribute
import org.simpleframework.xml.Root
import java.lang.Integer.min
import kotlin.math.max

@Root(name = "sqlitedb", strict = false)
data class GISQLDB constructor(
    @field:Attribute(name = "zoom_type")
    @param:Attribute(name = "zoom_type")
    var zoomingType: ZoomingType = ZoomingType.AUTO,

    @field:Attribute(name = "max")
    @param:Attribute(name = "max")
    var maxZ: Int = 19,

    @field:Attribute(name = "min")
    @param:Attribute(name = "min")
    var minZ: Int = 0,

    @field:Attribute(name = "ratio")
    @param:Attribute(name = "ratio")
    var ratio: Int = 1
) {
    fun getLevel(lvl: Int): Int {
        //todo
        return when (zoomingType) {
            ZoomingType.SMART -> lvl
            ZoomingType.AUTO -> {
                min(max(lvl, minZ), maxZ)
            }
            else -> lvl
        }
    }
}