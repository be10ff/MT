package com.example.mt.model.xml

import org.simpleframework.xml.Attribute
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root

@Root(name = "Group", strict = false)
data class GIPropertiesGroup @JvmOverloads constructor(
    @field:Attribute(name = "name")
    @param:Attribute(name = "name")
    var name: String?,

    @field:Attribute(name = "opacity")
    @param:Attribute(name = "opacity")
    var opacity: Float = 0f,

    @field:Attribute(name = "enabled")
    @param:Attribute(name = "enabled")
    var enabled: Boolean = true,

    @field:ElementList(inline = true, required = false)
    @param:ElementList(inline = true, required = false)
    val layers: List<GIPropertiesLayer> = emptyList()
)