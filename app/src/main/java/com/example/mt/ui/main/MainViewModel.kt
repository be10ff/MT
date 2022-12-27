package com.example.mt.ui.main

//import com.example.mt.ui.main.usecase.ReReDrawMap
import android.Manifest
import android.app.Application
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.util.Log
import androidx.lifecycle.*
import com.example.mt.data.FilesPermissionStatusListener
import com.example.mt.data.MtLocationListener
import com.example.mt.data.PermissionStatusListener
import com.example.mt.functional.AppCoroutineDispatchers
import com.example.mt.functional.ErrorHandlerManager
import com.example.mt.model.Action
import com.example.mt.model.MainState
import com.example.mt.model.MapState
import com.example.mt.model.gi.Bounds
import com.example.mt.model.gi.Project
import com.example.mt.model.gi.Projection
import com.example.mt.model.mapper.ProjectMapper
import com.example.mt.model.xml.GIPropertiesProject
import kotlinx.coroutines.launch
import org.simpleframework.xml.Serializer
import org.simpleframework.xml.core.Persister
import java.io.File

class MainViewModel(application: Application) : AndroidViewModel(application) {
    val dispatchers = AppCoroutineDispatchers()
    val errorHandlerManager = ErrorHandlerManager()

    //    val reDrawUsecase = ReDrawMap(dispatchers)
//    val reDrawUsecase = ReReDrawMap(dispatchers, errorHandlerManager)
    val locationPermissionStatusLiveData =
        PermissionStatusListener(application, Manifest.permission.ACCESS_FINE_LOCATION)
    val readStoragePermissionStatusLiveData =
        PermissionStatusListener(application, Manifest.permission.READ_EXTERNAL_STORAGE)
    val manageFilesPermissionStatusLiveData =
        FilesPermissionStatusListener(application)
    val gpsDataLiveData = MtLocationListener(application)

    val bitmapState: MutableLiveData<Bitmap> =
        MutableLiveData(/*Bitmap.createBitmap(0, 0, Bitmap.Config.ARGB_8888)*/)

    val projectState: MutableLiveData<Project> = MutableLiveData()

    //    val bitmapState: SingleLiveEvent<Bitmap> = SingleLiveEvent()
    val mainState: MutableLiveData<MainState> by lazy {
        MutableLiveData<MainState>().apply {
            postValue(
                MainState(
                    gpsState = false,
                    buttonState = false,
                    location = null,
                    storageGranted = false,
                    mapState = MapState(
                        bounds = Bounds(
                            Projection.WGS84,
                            28.0,
                            65.0,
                            48.0,
                            46.0
                        ).reproject(Projection.WorldMercator),
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
//                projectState.value?.let{
//                    saveProject(it)
//                }
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
                    handleReDraw(it.bounds, it.viewRect)
                }
            }

            is Action.MapAction.InitViewRect -> {
                updateState { it.copy(mapState = it.mapState.copy(viewRect = action.rect)) }
            }

            is Action.MapAction.InitMapView -> {
//                bitmapState.postValue(action.bitmap)
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
                        handleReDraw(it.bounds, it.viewRect)
                    }
                }
            }

            is Action.MapAction.MoveViewBy -> {
                mainState.value?.mapState?.let { mapState ->
                    Log.d("TOUCH", "update move " + this)
                    val bounds = Bounds(
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
                    handleReDraw(bounds, mapState.viewRect)

                }
            }

            is Action.MapAction.ScaleMapBy -> {
                action.focus
                mainState.value?.mapState?.let { mapState ->
                    val newFocusX =
                        mapState.bounds.left + mapState.pixelWidth * (action.focus.x - mapState.viewRect.left)
                    val newFocusY =
                        mapState.bounds.top - mapState.pixelHeight * (action.focus.y - mapState.viewRect.top)

                    var newLeft =
                        action.focus.x - (action.focus.x - mapState.viewRect.left).toDouble() / action.factor
                    var newTop =
                        action.focus.y - (action.focus.y - mapState.viewRect.top).toDouble() / action.factor
                    var newRight =
                        action.focus.x - (action.focus.x - mapState.viewRect.right).toDouble() / action.factor
                    var newBottom =
                        action.focus.y - (action.focus.y - mapState.viewRect.bottom).toDouble() / action.factor

                    val pixW = newRight - newLeft
                    val pixH = newBottom - newTop

                    when {
                        pixW / pixH > mapState.ratio -> {
                            val diff =
                                ((pixW / mapState.viewRect.width()) * mapState.viewRect.height() - pixH) / 2
                            newTop -= diff
                            newBottom += diff
                        }
                        pixW / pixH < mapState.ratio -> {
                            val diff =
                                ((pixH / mapState.viewRect.height()) * mapState.viewRect.width() - pixW) / 2
                            newLeft -= diff
                            newRight += diff
                        }
                    }

                    val newBounds = Bounds(
                        mapState.bounds.projection,
                        newFocusX - (action.focus.x - newLeft) * mapState.pixelWidth,
                        newFocusY + (action.focus.y - newTop) * mapState.pixelHeight,
                        newFocusX - (action.focus.x - newRight) * mapState.pixelWidth,
                        newFocusY + (action.focus.y - newBottom) * mapState.pixelHeight
                    )

                    updateState {
                        it.copy(
                            mapState = it.mapState.copy(
                                bounds = newBounds
                            )
                        )
                    }
                    handleReDraw(newBounds, mapState.viewRect)
//                    submitAction(Action.MapAction.Update)
                }
            }

            is Action.MapAction.MoveMapBy -> {

            }
            is Action.MapAction.Update -> {
                updateState { it.copy(mapState = it.mapState.copy(update = true)) }
                mainState.value?.mapState?.let {
                    handleReDraw(it.bounds, it.viewRect)
                }
            }

            is Action.ProjectAction.Load -> {
                loadProject()
            }
        }
    }

    private fun updateState(update: (MainState) -> MainState) {
        mainState.value?.let { viewModelScope.launch { mainState.value = update(it) } }
    }

    private fun handleReDraw(bounds: Bounds, rect: Rect) {
        viewModelScope.launch {
            if (rect.height() > 0 && rect.width() > 0) {
                val screen = Bitmap.createBitmap(
                    rect.width(),
                    rect.height(),
                    Bitmap.Config.ARGB_8888
                )
                projectState.value?.layers?.mapNotNull { layer ->
                    layer.renderBitmap(
                        bounds,
                        Rect(0, 0, rect.width(), rect.height()),
                        0,
                        0.0
                    )
                }
                    ?.fold(screen) { acc, bitmap ->
                        val canvas = Canvas(acc)
                        canvas.drawBitmap(bitmap, 0f, 0f, null)
                        acc
                    }
                    ?.let { bitmap ->
                        bitmapState.postValue(bitmap)
                    }
            }
        }
    }

    private fun adjustBoundsRatio(bounds: Bounds, screenRect: Rect): Bounds {
        val scrRatio = screenRect.width().toDouble() / screenRect.height()
        val areaRatio = bounds.width / bounds.height
        return when {
            areaRatio > scrRatio -> {
                val diff = (bounds.width / scrRatio - bounds.height) / 2
                Bounds(
                    bounds.projection,
                    bounds.left,
                    bounds.top + diff,
                    bounds.right,
                    bounds.bottom - diff
                )
            }
            areaRatio < scrRatio -> {
                val diff = (bounds.height * scrRatio - bounds.width) / 2
                Bounds(
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

    private fun loadProject() {
        viewModelScope.launch {
            val serializer: Serializer = Persister()
            val mapper = ProjectMapper()
            val input = File("storage/emulated/0/razan.pro")
            if (input.exists()) {
                serializer.read(GIPropertiesProject::class.java, input)
                    .let {
                        mapper.mapFrom(it)
                    }
                    .also {
                        projectState.postValue(it)
                    }
            }
        }
    }

    fun saveProject(project: Project) {
        viewModelScope.launch {
            val serializer: Serializer = Persister()
            val mapper = ProjectMapper()
            val output = File("storage/emulated/0/" + project.saveAs)
            if (!output.exists()) output.createNewFile()
            val outputProject = mapper.mapTo(project)
            serializer.write(outputProject, output)
        }
    }

    val proj = "<?xml version='1.0' encoding='UTF-8' standalone='yes' ?>\n" +
            "<Project name=\"YarilinaPlesh\" SaveAs=\"YarilinaPlesh.pro\" ID=\"43\">\n" +
            "  <Description text=\"YarilinaPlesh\" />\n" +
//            "  <Map>\n" +
            "    <Group name=\"\" opacity=\"1.0\" enabled=\"true\" obscure=\"false\">\n" +
            "      <Layer name=\"Worlds.sqlitedb\" type=\"SQL_YANDEX_LAYER\" enabled=\"true\">\n" +
            "        <Source location=\"absolute\" name=\"/storage/emulated/0/Worlds.sqlitedb\" />\n" +
            "        <sqlitedb zoom_type=\"AUTO\" min=\"1\" max=\"19\" ratio=\"1\" />\n" +
            "        <Range from=\"233014197\" to=\"888\" />\n" +
            "      </Layer>\n" +
            "      <Layer name=\"50rus.sqlitedb\" type=\"SQL_YANDEX_LAYER\" enabled=\"true\">\n" +
            "        <Source location=\"absolute\" name=\"/storage/emulated/0/50rus.sqlitedb\" />\n" +
            "        <sqlitedb zoom_type=\"SMART\" min=\"0\" max=\"16\" ratio=\"3\" />\n" +
            "        <Range from=\"466028394\" to=\"1777\" />\n" +
            "      </Layer>" +
            "    </Group>\n" +
//            "  </Map>\n" +
            "  <Bounds projection=\"WGS84\" top=\"56.88694295354085\" bottom=\"56.7455951576742\" left=\"38.526824200875275\" right=\"39.017741885987796\" />\n" +
            "  <Markers file=\"YarilinaPlesh_Nov_02_poi.xml\" source=\"layer\" />\n" +
            "</Project>"


}