package com.example.mt.ui.main

//import com.example.mt.ui.main.usecase.ReReDrawMap
import android.Manifest
import android.app.Application
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.os.Environment
import android.util.Log
import androidx.lifecycle.*
import com.example.mt.CommonUtils
//import androidx.lifecycle.*
import com.example.mt.data.FilesPermissionStatusListener
import com.example.mt.data.MtLocationListener
import com.example.mt.data.PermissionStatusListener
import com.example.mt.functional.AppCoroutineDispatchers
import com.example.mt.functional.ErrorHandlerManager
import com.example.mt.functional.combineLatest
import com.example.mt.functional.filter
import com.example.mt.map.MapUtils
import com.example.mt.map.layer.Layer
import com.example.mt.map.layer.XMLLayer
import com.example.mt.map.wkt.DBaseField
import com.example.mt.map.wkt.WktGeometry
import com.example.mt.map.wkt.WktPoint
import com.example.mt.map.wkt.WktTrack
import com.example.mt.model.Action
import com.example.mt.model.ButtonState
import com.example.mt.model.ButtonState.Companion.InitialState
import com.example.mt.model.MainState
import com.example.mt.model.MapState
import com.example.mt.model.gi.Bounds
import com.example.mt.model.gi.GILonLat
import com.example.mt.model.gi.Project
import com.example.mt.model.gi.Projection
import com.example.mt.model.mapper.ProjectMapper
import com.example.mt.model.xml.GIPropertiesProject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.simpleframework.xml.Serializer
import org.simpleframework.xml.core.Persister
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

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

    var poiLayer: XMLLayer? = null
    var trackLayer: XMLLayer? = null
    var currentTrack: WktGeometry? = null


    //    val bitmapState: SingleLiveEvent<Bitmap> = SingleLiveEvent()
    val mainState: MutableLiveData<MainState> by lazy {
        MutableLiveData<MainState>().apply {
            postValue(
                MainState(
                    gpsState = false,
                    buttonState = InitialState,
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

    val buttonState: LiveData<ButtonState> =
        mainState.map {
            it.buttonState
        }

    val trackingState = gpsState
        .filter { it != null }
        .combineLatest(buttonState.filter { it != null }) { location, buttonState ->
            location to buttonState
        }

    init {
        trackingState.distinctUntilChanged()
            .observeForever { (location, state) ->
                when {
                    state?.writeTrack ?: false -> {
                        location?.let {
                            val point = WktPoint(GILonLat(location.longitude, location.latitude))
                                .apply {
                                    this.attributes.put(
                                        "Date",
                                        DBaseField("Date", CommonUtils.currentTime())
                                    )
                                }
                            (currentTrack as? WktTrack)?.let { track ->
                                if (track.points.size == 0 || (track.points.size > 0 && MapUtils(
                                        track.points[track.points.size - 1].point,
                                        point.point
                                    ).getDistance() > 2 * location.accuracy)
                                ) {
                                    track.points.add(point)
                                    track.append(point.toWKT())
                                }
                            }
                        }
                    }
                    state?.follow ?: false -> {}
                }
            }

        projectState.distinctUntilChanged().observeForever { reDraw() }

    }

    fun reDraw() {
        mainState.value?.mapState?.let {
            handleReDraw(it.bounds, it.viewRect)
        }
    }

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

                reDraw()
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
                    reDraw()
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

            is Action.MapAction.ScaleMapBy -> handleScale(action)

            is Action.MapAction.MoveMapBy -> {

            }
            is Action.MapAction.Update -> {
                updateState { it.copy(mapState = it.mapState.copy(update = true)) }
            }

            is Action.ProjectAction.Load -> {
                loadProject()
            }

            is Action.ProjectAction.Save -> {
                projectState.value?.let { saveProject(it) }
            }

            is Action.ButtonAction.WriteTrack -> {
                updateState { it.copy(buttonState = it.buttonState.copy(writeTrack = !it.buttonState.writeTrack)) }
                if (mainState.value?.buttonState?.writeTrack == true) handleCreateTrack() else handleStopTrack()
            }

            is Action.ButtonAction.FollowPosition -> {
                updateState { it.copy(buttonState = it.buttonState.copy(follow = !it.buttonState.follow)) }
            }

            is Action.ButtonAction.AddPosition -> {
                handleCreatePoi()
            }
        }
    }

    private fun updateState(update: (MainState) -> MainState) {
        mainState.value?.let { viewModelScope.launch { mainState.value = update(it) } }
    }

    private fun updateProjectState(update: (Project) -> Project) {
        projectState.value?.let { viewModelScope.launch { projectState.value = update(it) } }
    }

    private fun handleScale(action: Action.MapAction.ScaleMapBy) {
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
        }
    }

    private fun handleReDraw(bounds: Bounds, rect: Rect) {
        viewModelScope.launch {
            launch(Dispatchers.IO) {
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
                            1f
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
    }

    private fun handleCreatePoi() {
        if (poiLayer == null) {
            poiLayer = handleCreatePoiLayer()
        }
        mainState.value?.mapState?.bounds?.let {
            val poi = WktPoint(Projection.reproject(it.center, it.projection, Projection.WGS84))
                .apply {
                    this.attributes.put("Date", DBaseField("Date", CommonUtils.currentTime()))
                    this.attributes.put(
                        "Project",
                        DBaseField("Project", projectState.value?.name ?: "Track")
                    )
                }
            poiLayer?.let { layer ->
                layer.geometries.add(poi)
                //Todo
                reDraw()
                startEditingPoi(layer, poi)
            }

        }
    }

    private fun handleCreateTrack() {
        if (trackLayer == null) {
            trackLayer = handleCreateTrackLayer()
        }

        trackLayer?.let { layer ->
            val format = SimpleDateFormat("MMM_dd_mm_ss", Locale.ENGLISH)
            val dateString = format.format(Date(Calendar.getInstance().timeInMillis))
            val nameString = "${projectState.value?.name}_${dateString}.track"
            val source =
                Environment.getExternalStorageDirectory().absolutePath + File.separator + (projectState.value?.name
                    ?: "Track") + File.separator + nameString
            val output = File(source)
            if (!output.exists()) output.createNewFile()
            currentTrack = WktTrack(source)
                .apply {
                    this.attributes.put("Date", DBaseField("Date", CommonUtils.currentTime()))
                    this.attributes.put(
                        "Project",
                        DBaseField("Project", projectState.value?.name ?: "Track")
                    )
                }
                .also {
                    layer.geometries.add(it)
                    layer.save()
                }
        }
    }

    private fun handleStopTrack() {
        (currentTrack as? WktTrack)?.let { track ->
            track.stop()
            currentTrack = null
        } ?: run {

        }
    }

    private fun startEditingPoi(layer: XMLLayer, point: WktPoint) {
        //ToDo
        poiLayer?.save()
    }

    private fun handleCreatePoiLayer(name: String? = null): XMLLayer {
        val format = SimpleDateFormat("MMM_dd_yy", Locale.ENGLISH)
        val dateString = format.format(Date(Calendar.getInstance().timeInMillis))
        val nameString = "${projectState.value?.name}_${dateString}_poi.xml"

        val layer = Layer.createPoiLayer(projectState.value?.name ?: "Track", nameString)
            .also {
                it.save()
            }
        updateProjectState {
            it.copy(layers = it.layers.toMutableList().apply {
                add(layer)
            })
        }
        return layer
    }

    private fun handleCreateTrackLayer(name: String? = null): XMLLayer {
        val format = SimpleDateFormat("MMM_dd_yy", Locale.ENGLISH)
        val dateString = format.format(Date(Calendar.getInstance().timeInMillis))
        val nameString = "${projectState.value?.name}_${dateString}_track.xml"

        val layer = Layer.createTrackLayer(projectState.value?.name ?: "Track", nameString)
            .also {
                it.save()
            }
        updateProjectState {
            it.copy(layers = it.layers.toMutableList().apply {
                add(layer)
            })
        }
        return layer
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
            val input = File("storage/emulated/0/YarilinaPlesh.pro")
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
            withContext(dispatchers.io) {
                val serializer: Serializer = Persister()
                val mapper = ProjectMapper()
                val output = File("storage/emulated/0/" + project.saveAs)
                if (!output.exists()) output.createNewFile()
                mainState.value?.mapState?.bounds?.let {
                    project.copy(bounds = it)
                }?.also {
                    mapper.mapTo(it)
                        .also {
                            serializer.write(it, output)
                        }
                }
            }

        }
    }
}