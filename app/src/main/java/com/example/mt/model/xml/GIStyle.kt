package com.example.mt.model.xml

import org.simpleframework.xml.Attribute
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root

@Root(name = "Style ", strict = false)
data class GIStyle constructor(
    @field:Attribute(name = "type")
    @param:Attribute(name = "type")
    var type: String,

    @field:Attribute(name = "lineWidth")
    @param:Attribute(name = "lineWidth")
    var lineWidth: Float,

    @field:Attribute(name = "opacity")
    @param:Attribute(name = "opacity")
    var opacity: Float,

    @field:ElementList(inline = true, required = false)
    @param:ElementList(inline = true, required = false)
    val color: List<GIColor>?
)