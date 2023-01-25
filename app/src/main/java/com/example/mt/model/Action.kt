package com.example.mt.model

import android.graphics.Bitmap
import android.graphics.Point
import android.graphics.Rect
import android.location.Location
import com.example.mt.model.gi.Bounds

sealed class Action {
    sealed class GPSAction : Action() {
        data class GPSEnabled(val enabled: Boolean) : GPSAction()
        data class LocationUpdated(val location: Location) : GPSAction()
        data class StorageGranted(val granted: Boolean) : GPSAction()
    }

    sealed class MapAction : Action() {
        data class BoundsChanged(val bounds: Bounds) : MapAction()
        data class MoveViewBy(val x: Int, val y: Int) : MapAction()
        data class MoveMapBy(val x: Double, val y: Double) : MapAction()
        data class ScaleMapBy(val focus: Point, val factor: Float) : MapAction()
        data class ViewRectChanged(val rect: Rect) : MapAction()
        data class InitViewRect(val rect: Rect) : MapAction()
        data class InitMapView(val bitmap: Bitmap?) : MapAction()
        object Update : MapAction()
    }

    sealed class ProjectAction : Action() {
        data class Load(val source: String) : ProjectAction()
        object Save : ProjectAction()
    }

    sealed class ButtonAction : Action() {
        object WriteTrack : ButtonAction()
        object FollowPosition : ButtonAction()
        object AddPosition : ButtonAction()
    }

}
