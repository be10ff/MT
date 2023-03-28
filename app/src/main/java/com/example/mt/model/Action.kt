package com.example.mt.model

import android.graphics.Point
import android.graphics.Rect
import android.location.Location
import com.example.mt.map.layer.Layer
import com.example.mt.map.layer.XMLLayer
import com.example.mt.map.wkt.WktGeometry
import com.example.mt.map.wkt.WktPoint
import com.example.mt.model.xml.SqlProjection

sealed class Action {
    sealed class PermissionAction : Action() {
        data class GPSEnabled(val enabled: Boolean) : PermissionAction()
        data class ManageFileGranted(val granted: Status) : PermissionAction()
    }

    sealed class MapAction : Action() {
        data class MoveViewBy(val x: Int, val y: Int) : MapAction()
        data class MoveMapBy(val x: Double, val y: Double) : MapAction()
        data class ScaleMapBy(val focus: Point, val factor: Float) : MapAction()
        data class ViewRectChanged(val rect: Rect) : MapAction()
        object Update : MapAction()
        data class ClickAt(val point: Point) : MapAction()
    }

    sealed class ProjectAction : Action() {
        data class Load(val source: String) : ProjectAction()
        data class AddLayer(val source: String) : ProjectAction()
        object Save : ProjectAction()
        data class VisibilityChanged(val layer: Layer, val enabled: Boolean) : ProjectAction()
        data class RangeChanged(val layer: Layer, val from: Int, val to: Int) : ProjectAction()
        data class RemoveLayer(val layer: Layer) : Action()
        data class MoveLayer(val from: Layer, val to: Layer) : Action()
        data class TypeChanged(val layer: Layer, val type: SqlProjection) : Action()
        data class MarkersSourceSelected(val layer: XMLLayer) : Action()
        //ToDo same as data class SetPoi(val geometry: WktPoint) : GeometryAction()
        data class MarkersSelectionChanged(val point: WktPoint) : Action()
        data class NameChanged(val name: String) : Action()
        data class PathChanged(val path: String) : Action()
        data class DescriptionChanged(val description: String) : Action()
    }

    sealed class GeometryAction: Action(){
        data class Delete(val geometry: WktGeometry) : GeometryAction()
        data class Edit(val geometry: WktPoint) : GeometryAction()
        //ToDo same as  MarkersSelectionChanged(val point: WktPoint) : Action()
        data class SetPoi(val geometry: WktPoint) : GeometryAction()
        data class ChangeSelected(val geometry: WktPoint) : GeometryAction()
    }

    sealed class ButtonAction : Action() {
        object WriteTrack : ButtonAction()
        object FollowPosition : ButtonAction()
        object AddPosition : ButtonAction()
    }

}
