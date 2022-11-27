package com.example.mt.model

import android.graphics.Bitmap
import android.graphics.Rect
import android.location.Location
import com.example.mt.model.gi.GIBounds

sealed class Action {
    sealed class GPSAction : Action() {
        data class GPSEnabled(val enabled: Boolean) : GPSAction()
        data class LocationUpdated(val location: Location) : GPSAction()
        data class StorageGranted(val granted: Boolean) : GPSAction()
    }

    sealed class MapAction : Action() {
        data class BoundsChanged(val bounds: GIBounds) : MapAction()
        data class MoveViewBy(val x: Int, val y: Int) : MapAction()
        data class MoveMapBy(val x: Double, val y: Double) : MapAction()
        data class ViewRectChanged(val rect: Rect) : MapAction()
        data class InitViewRect(val rect: Rect) : MapAction()
        data class InitMapView(val bitmap: Bitmap?) : MapAction()
        object Update : MapAction()
    }

}
