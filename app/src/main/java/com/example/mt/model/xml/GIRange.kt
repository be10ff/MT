package com.example.mt.model.xml

import org.simpleframework.xml.Attribute
import org.simpleframework.xml.Root

@Root(name = "Range", strict = false)
data class GIRange constructor(
    @field:Attribute(name = "from")
    @param:Attribute(name = "from")
    var from: String,

    @field:Attribute(name = "to")
    @param:Attribute(name = "to")
    var to: String
)