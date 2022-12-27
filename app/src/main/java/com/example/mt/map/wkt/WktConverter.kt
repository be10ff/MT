package com.example.mt.map.wkt

import org.simpleframework.xml.convert.Converter
import org.simpleframework.xml.stream.InputNode
import org.simpleframework.xml.stream.OutputNode

class WktConverter : Converter<WktLayer> {
    override fun read(node: InputNode?): WktLayer {
        return buildList<WktGeometry> {
            do {
                val n = node?.next
                n?.let { node ->
                    node.getAttribute(XmlTag.GEOMETRY.tag).value
                        ?.let { input ->
                            when (node.name) {
                                WKTGeometryType.POINT.name -> input.pointFromWkt()

                                WKTGeometryType.TRACK.name -> {
                                    WktTrack(input)
                                }
                                else -> null
                            }
                        }
                        ?.let { geometry ->
                            val attributes = node.attributes
                                .filter {
                                    it != XmlTag.GEOMETRY.tag
                                }
                                .map { attribute ->
                                    attribute to DBaseField(
                                        attribute,
                                        node.getAttribute(attribute).value
                                    )
                                }
                            geometry.attributes.putAll(attributes)
                            geometry
                        }
                        ?.let {
                            this.add(it)
                        }
                }
            } while (n != null)
        }.let {
            WktLayer(it)
        }

    }

    override fun write(node: OutputNode?, value: WktLayer?) {
        value?.let {
            it.geometry.forEach { geometry ->
                node?.getChild(geometry.type.name)
                    ?.apply {
                        setAttribute(XmlTag.GEOMETRY.tag, geometry.toWKT())
                        geometry.attributes.forEach { (name, field) ->
                            setAttribute(name, field.value)
                        }
                    }
            }
        }
    }
}