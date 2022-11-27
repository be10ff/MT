package com.example.mt.ui.main

import android.Manifest
import android.app.Application
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import android.util.Log
import androidx.lifecycle.*
import com.example.mt.data.MtLocationListener
import com.example.mt.data.PermissionStatusListener
import com.example.mt.functional.AppCoroutineDispatchers
import com.example.mt.functional.ErrorHandlerManager
import com.example.mt.functional.SingleLiveEvent
import com.example.mt.map.layer.GILayer
import com.example.mt.model.Action
import com.example.mt.model.MainState
import com.example.mt.model.MapState
import com.example.mt.model.gi.GIBounds
import com.example.mt.model.gi.GIProjection
import com.example.mt.ui.main.usecase.ReReDrawMap
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class MainViewModel(application: Application) : AndroidViewModel(application) {
    val dispatchers = AppCoroutineDispatchers()
    val errorHandlerManager = ErrorHandlerManager()

    //    val reDrawUsecase = ReDrawMap(dispatchers)
    val reDrawUsecase = ReReDrawMap(dispatchers, errorHandlerManager)
    val locationPermissionStatusLiveData =
        PermissionStatusListener(application, Manifest.permission.ACCESS_FINE_LOCATION)
    val readStoragePermissionStatusLiveData =
        PermissionStatusListener(application, Manifest.permission.READ_EXTERNAL_STORAGE)
    val gpsDataLiveData = MtLocationListener(application)

    //    val bitmapState: MutableLiveData<Bitmap> = MutableLiveData(/*Bitmap.createBitmap(0, 0, Bitmap.Config.ARGB_8888)*/)
    val bitmapState: SingleLiveEvent<Bitmap> = SingleLiveEvent()
    val mainState: MutableLiveData<MainState> by lazy {
        MutableLiveData<MainState>().apply {
            postValue(
                MainState(
                    gpsState = false,
                    buttonState = false,
                    location = null,
                    storageGranted = false,
                    mapState = MapState(
                        bounds = GIBounds(
                            GIProjection.WGS84,
                            28.0,
                            65.0,
                            48.0,
                            46.0
                        ).reproject(GIProjection.WorldMercator),
//                        position = Location(),
                        zoom = 1f,
                        viewRect = Rect(0, 0, 500, 500),
                        update = false
                    )
                )
            )
        }
    }
    val gpsState = mainState.map {
        it.location
    }.distinctUntilChanged()

    val storageState = mainState.map {
        it.storageGranted
    }.distinctUntilChanged()


    fun submitAction(action: Action) {
        when (action) {
            is Action.GPSAction.GPSEnabled -> updateState { it.copy(gpsState = true) }

            is Action.GPSAction.LocationUpdated -> {
                updateState { it.copy(location = action.location) }
            }

            is Action.GPSAction.StorageGranted -> {
                updateState { it.copy(storageGranted = action.granted) }
            }

            is Action.MapAction.BoundsChanged -> {
                updateState {
                    it.copy(
                        mapState = it.mapState.copy(
                            bounds = action.bounds,
                            update = false
                        )
                    )
                }
                mainState.value?.mapState?.let {
//                    handleReDraw(it.bounds, it.viewRect)
                }
            }

            is Action.MapAction.InitViewRect -> {
                updateState { it.copy(mapState = it.mapState.copy(viewRect = action.rect)) }
            }

            is Action.MapAction.InitMapView -> {
                bitmapState.postValue(action.bitmap)
            }

            is Action.MapAction.ViewRectChanged -> {
                mainState.value?.mapState?.let { mapState ->
                    val bounds = adjustBoundsRatio(mapState.bounds, action.rect)
                    updateState {
                        it.copy(
                            mapState = it.mapState.copy(
                                viewRect = action.rect,
                                bounds = bounds,
                                update = false
                            )
                        )
                    }
                    mainState.value?.mapState?.let {
//                        handleReDraw(it.bounds, it.viewRect)
                    }
                }
            }

            is Action.MapAction.MoveViewBy -> {
                mainState.value?.mapState?.let { mapState ->
                    Log.d("TOUCH", "update move " + this)
                    val bounds = GIBounds(
                        mapState.bounds.projection,
                        mapState.bounds.left + action.x * mapState.pixelWidth,
                        mapState.bounds.top + action.y * mapState.pixelHeight,
                        mapState.bounds.right + action.x * mapState.pixelWidth,
                        mapState.bounds.bottom + action.y * mapState.pixelHeight
                    )
                    updateState {
                        it.copy(
                            mapState = it.mapState.copy(
                                bounds = bounds,
                                update = false
                            )
                        )
                    }
                    mainState.value?.mapState?.let {
//                        handleReDraw(it.bounds, it.viewRect)
                    }
                }
            }

            is Action.MapAction.MoveMapBy -> {

            }
            is Action.MapAction.Update -> {
                updateState { it.copy(mapState = it.mapState.copy(update = true)) }
                mainState.value?.mapState?.let {
//                    handleReDraw(it.bounds, it.viewRect)
                }
            }
        }
    }

    private fun updateState(update: (MainState) -> MainState) {
//        Log.d("THREAD", "state update" + this )
        mainState.value?.let { viewModelScope.launch { mainState.value = update(it) } }
    }

    private fun handleReDraw(bounds: GIBounds, rect: Rect) {
        viewModelScope.launch {
            bitmapState.value?.let {
                reDrawUsecase(ReReDrawMap.Params(it, bounds, rect))
                    .either(
                        ifLeft = {

                        },
                        ifRight = {
                            val res = it
                            bitmapState.postValue(it)


                        }
                    )
            }
        }

        viewModelScope.launch {
            bitmapState.value?.let {
                runBlocking {
                    Log.d("TOUCH", "ReReDrawMap " + this)
                    bitmapState.value?.apply {
                        eraseColor(Color.WHITE)
                        Canvas(this).let { canvas ->
                            GILayer.sqlTest.redraw(bounds, this, 0, 0.0)
                            canvas.drawBitmap(this, rect, rect, null)
                        }
                    }
                }
            }
        }
    }

    private fun adjustBoundsRatio(bounds: GIBounds, screenRect: Rect): GIBounds {
        val scrRatio = screenRect.width().toDouble() / screenRect.height()
        val areaRatio = bounds.width / bounds.height
        return when {
            areaRatio > scrRatio -> {
                val diff = (bounds.width / scrRatio - bounds.height) / 2
                GIBounds(
                    bounds.projection,
                    bounds.left,
                    bounds.top + diff,
                    bounds.right,
                    bounds.bottom - diff
                )
            }
            areaRatio < scrRatio -> {
                val diff = (bounds.height * scrRatio - bounds.width) / 2
                GIBounds(
                    bounds.projection,
                    bounds.left - diff,
                    bounds.top,
                    bounds.right + diff,
                    bounds.bottom
                )
            }
            else -> {
                bounds
            }
        }

    }
}