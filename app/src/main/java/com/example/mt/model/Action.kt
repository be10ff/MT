package com.example.mt.model

import android.graphics.Point
import android.graphics.Rect
import android.location.Location
import com.example.mt.map.layer.Layer
import com.example.mt.model.xml.SqlProjection

sealed class Action {
    sealed class GPSAction : Action() {
        data class GPSEnabled(val enabled: Boolean) : GPSAction()
        data class LocationUpdated(val location: Location) : GPSAction()

        //        data class LonLatUpdated(val lonLat: GILonLat) : GPSAction()
        data class StorageGranted(val granted: Boolean) : GPSAction()
    }

    sealed class MapAction : Action() {
        data class MoveViewBy(val x: Int, val y: Int) : MapAction()
        data class MoveMapBy(val x: Double, val y: Double) : MapAction()
        data class ScaleMapBy(val focus: Point, val factor: Float) : MapAction()
        data class ViewRectChanged(val rect: Rect) : MapAction()
        object Update : MapAction()
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
    }

    sealed class ButtonAction : Action() {
        object WriteTrack : ButtonAction()
        object FollowPosition : ButtonAction()
        object AddPosition : ButtonAction()
    }

}
