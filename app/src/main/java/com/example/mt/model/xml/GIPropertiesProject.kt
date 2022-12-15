package com.example.mt.model.xml

import org.simpleframework.xml.Attribute
import org.simpleframework.xml.Element
import org.simpleframework.xml.Root

@Root(name = "Project", strict = false)
data class GIPropertiesProject /*@JvmOverloads*/ constructor(
    @field:Attribute(name = "name")
    @param:Attribute(name = "name")
    var name: String?,

    @field:Attribute(name = "SaveAs")
    @param:Attribute(name = "SaveAs")
    var saveAs: String,

    @field:Element(name = "Group")
    @param:Element(name = "Group")
    var group: GIPropertiesGroup,

    @field:Element(name = "Description", required = false)
    @param:Element(name = "Description")
    var description: GIPropertiesDescription?,

    @field:Element(name = "Bounds")
    @param:Element(name = "Bounds")
    var bounds: GIBounds,

    @field:Element(name = "Markers")
    @param:Element(name = "Markers")
    var markers: GIMarker
)