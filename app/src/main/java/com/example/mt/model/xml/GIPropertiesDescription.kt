package com.example.mt.model.xml

import org.simpleframework.xml.Attribute
import org.simpleframework.xml.Root

@Root(name = "Description", strict = false)
data class GIPropertiesDescription constructor(
    @field:Attribute(name = "text")
    @param:Attribute(name = "text")
    var text: String?
)