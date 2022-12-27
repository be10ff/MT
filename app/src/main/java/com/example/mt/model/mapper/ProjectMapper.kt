package com.example.mt.model.mapper

import com.example.mt.map.layer.SQLLayer
import com.example.mt.map.layer.TrafficLayer
import com.example.mt.map.layer.XMLLayer
import com.example.mt.model.gi.Bounds
import com.example.mt.model.gi.Project
import com.example.mt.model.gi.Projection
import com.example.mt.model.gi.VectorStyle
import com.example.mt.model.xml.*

class ProjectMapper {
    fun mapFrom(from: GIPropertiesProject): Project {
        return Project(
            name = from.name,
            saveAs = from.saveAs,
            description = from.description?.text,
            bounds = with(from.bounds) {
                Bounds(
                    projection = Projection.of(projection),
                    left = left,
                    top = top,
                    right = right,
                    bottom = bottom
                )
            },
            layers = from.group.layers
                .map {
                    when (it.type) {
                        GILayerType.XML -> XMLLayer(
                            name = it.name,
                            type = it.type,
                            enabled = it.enabled,
                            source = it.source.name,
                            sourceLocation = it.source.location,
                            rangeFrom = it.range.from.toIntOrNull(),
                            rangeTo = it.range.to.toIntOrNull(),
                            //todo
//                            style =  it.style!!,
                            style = VectorStyle.default,
                            editableType = it.editable?.type,
                            activeEdiable = it.editable?.active
                        )

                        GILayerType.ON_LINE -> TrafficLayer(
                            name = it.name,
                            type = it.type,
                            enabled = it.enabled,
                            source = it.source.name,
                            sourceLocation = it.source.location,
                            rangeFrom = it.range.from.toIntOrNull(),
                            rangeTo = it.range.to.toIntOrNull(),
                        )

                        else -> SQLLayer(
                            name = it.name,
                            type = it.type,
                            enabled = it.enabled,
                            source = it.source.name,
                            sourceLocation = it.source.location,
                            rangeFrom = it.range.from.toIntOrNull(),
                            rangeTo = it.range.to.toIntOrNull(),
                            sqldb = it.sqlDb!!
                        )
                    }

                },
            markerFile = from.markers.file,
            markerSource = from.markers.file
        )
    }

    fun mapTo(from: Project): GIPropertiesProject {
        return GIPropertiesProject(
            name = from.name,
            saveAs = from.saveAs,
            group = GIPropertiesGroup(
                name = "",
                opacity = 0f,
                enabled = true,
                layers = from.layers
                    .map {
                        GIPropertiesLayer(
                            name = it.name,
                            enabled = it.enabled,
                            type = it.type,
                            source = GISource(
                                name = it.source,
                                location = it.sourceLocation
                            ),
                            sqlDb = (it as? SQLLayer)?.sqldb,
                            //todo
//                            style = (it as? XMLLayer)?.style,
                            style = null,
                            range = GIRange(
                                from = it.rangeFrom?.toString() ?: "NAN",
                                to = it.rangeTo?.toString() ?: "NAN"
                            ),
                            editable = (it as? XMLLayer)?.let { xmlProperties ->
                                xmlProperties.editableType?.let { type ->
                                    xmlProperties.activeEdiable?.let { active ->
                                        GIEditable(
                                            type = type,
                                            active = active
                                        )
                                    }
                                }

                            }

                        )
                    }
            ),
            bounds = with(from.bounds) {
                GIBounds(
                    projection = projection.name,
                    left = left,
                    top = top,
                    right = right,
                    bottom = bottom
                )
            },
            description = GIPropertiesDescription(from.description),
            markers = GIMarker(
                file = from.markerFile,
                source = from.markerSource
            )
        )
    }
}