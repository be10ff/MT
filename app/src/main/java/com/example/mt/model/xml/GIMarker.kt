package com.example.mt.model.xml

import org.simpleframework.xml.Attribute
import org.simpleframework.xml.Root

@Root(name = "Markers", strict = false)
data class GIMarker constructor(
    @field:Attribute(name = "file")
    @param:Attribute(name = "file")
    var file: String,

    @field:Attribute(name = "source")
    @param:Attribute(name = "source")
    var source: String
)