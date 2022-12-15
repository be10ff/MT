package com.example.mt.model.xml

import org.simpleframework.xml.Attribute
import org.simpleframework.xml.Root

@Root(name = "Bounds", strict = false)
data class GIBounds(
    @field:Attribute(name = "projection")
    @param:Attribute(name = "projection")
    val projection: String,

    @field:Attribute(name = "left")
    @param:Attribute(name = "left")
    val left: Double,

    @field:Attribute(name = "top")
    @param:Attribute(name = "top")
    val top: Double,

    @field:Attribute(name = "right")
    @param:Attribute(name = "right")
    val right: Double,

    @field:Attribute(name = "bottom")
    @param:Attribute(name = "bottom")
    val bottom: Double
)