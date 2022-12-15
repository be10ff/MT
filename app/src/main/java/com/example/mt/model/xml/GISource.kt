package com.example.mt.model.xml

import org.simpleframework.xml.Attribute
import org.simpleframework.xml.Root

@Root(name = "Source", strict = false)
data class GISource constructor(
    @field:Attribute(name = "name")
    @param:Attribute(name = "name")
    var name: String,

    @field:Attribute(name = "location")
    @param:Attribute(name = "location")
    var location: SourceLocation
)