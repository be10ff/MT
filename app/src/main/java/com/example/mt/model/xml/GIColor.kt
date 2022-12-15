package com.example.mt.model.xml

import org.simpleframework.xml.Attribute
import org.simpleframework.xml.Root

@Root(name = "Color", strict = false)
data class GIColor constructor(
    @field:Attribute(name = "description")
    @param:Attribute(name = "description")
    var description: String,

    @field:Attribute(name = "name")
    @param:Attribute(name = "name")
    var name: String,

    @field:Attribute(name = "r")
    @param:Attribute(name = "r")
    var r: Int,

    @field:Attribute(name = "g")
    @param:Attribute(name = "g")
    var g: Int,

    @field:Attribute(name = "b")
    @param:Attribute(name = "b")
    var b: Int,

    @field:Attribute(name = "a")
    @param:Attribute(name = "a")
    var a: Int,
)