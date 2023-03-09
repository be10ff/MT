package com.example.mt.model.xml

import org.simpleframework.xml.Attribute
import org.simpleframework.xml.Element
import org.simpleframework.xml.Root

@Root(name = "Layer", strict = false)
data class GIPropertiesLayer constructor(
    @field:Attribute(name = "name")
    @param:Attribute(name = "name")
    var name: String?,

    @field:Attribute(name = "enabled")
    @param:Attribute(name = "enabled")
    var enabled: Boolean = true,

    @field:Attribute(name = "type")
    @param:Attribute(name = "type")
    var type: GILayerType,

    @field:Element(name = "Source")
    @param:Element(name = "Source")
    var source: GISource,

    @field:Attribute(name = "sqlProjection", required = false)
    @param:Attribute(name = "sqlProjection", required = false)
    var sqlProjection: SqlProjection?,

    @field:Element(name = "sqlitedb", required = false)
    @param:Element(name = "sqlitedb", required = false)
    var sqlDb: GISQLDB?,

    @field:Element(name = "Range", required = false)
    @param:Element(name = "Range", required = false)
    var range: GIRange,

    @field:Element(name = "Style", required = false)
    @param:Element(name = "Style", required = false)
    var style: GIStyle?,

    @field:Element(name = "Editable", required = false)
    @param:Element(name = "Editable", required = false)
    var editable: GIEditable?
)