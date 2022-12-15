package com.example.mt.model.xml

import org.simpleframework.xml.Attribute
import org.simpleframework.xml.Root

@Root(name = "Editable", strict = false)
data class GIEditable constructor(
    @field:Attribute(name = "type")
    @param:Attribute(name = "type")
    var type: EditableType,

    @field:Attribute(name = "active")
    @param:Attribute(name = "active")
    var active: Boolean
)