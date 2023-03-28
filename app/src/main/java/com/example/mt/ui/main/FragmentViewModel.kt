package com.example.mt.ui.main

import android.Manifest
import android.app.Application
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Point
import android.graphics.Rect
import android.os.Environment
import android.util.Log
import android.view.ViewConfiguration
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mt.CommonUtils
import com.example.mt.data.locationListener
import com.example.mt.data.managePermission
import com.example.mt.data.permission
import com.example.mt.data.sensorListener
import com.example.mt.functional.AppCoroutineDispatchers
import com.example.mt.functional.ErrorHandlerManager
import com.example.mt.functional.updateFlow
import com.example.mt.functional.windowed
import com.example.mt.map.MapUtils
import com.example.mt.map.Screen
import com.example.mt.map.layer.Layer
import com.example.mt.map.layer.SQLLayer
import com.example.mt.map.layer.XMLLayer
import com.example.mt.map.wkt.DBaseField
import com.example.mt.map.wkt.WktGeometry
import com.example.mt.map.wkt.WktPoint
import com.example.mt.map.wkt.WktTrack
import com.example.mt.model.*
import com.example.mt.model.gi.Bounds
import com.example.mt.model.gi.GILonLat
import com.example.mt.model.gi.Project
import com.example.mt.model.gi.Projection
import com.example.mt.model.mapper.ProjectMapper
import com.example.mt.model.xml.GIPropertiesProject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.simpleframework.xml.Serializer
import org.simpleframework.xml.core.Persister
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.hypot

class FragmentViewModel(application: Application) : AndroidViewModel(application) {
    val dispatchers = AppCoroutineDispatchers()
    val errorHandlerManager = ErrorHandlerManager()

    val locationPermissionStatusFlow = application.permission(
        Manifest.permission.ACCESS_FINE_LOCATION
    )
    private val touchSlop = ViewConfiguration.get(application).scaledTouchSlop
    val manageFilesPermissionStatusFlow = application.managePermission()

    private val invalidationState = MutableStateFlow<Boolean>(false)
    private val _projectState: MutableStateFlow<Project> = MutableStateFlow(Project.InitialState)
    val projectState: StateFlow<Project> = _projectState
    val bitmapState: Flow<BitmapState> =
        _projectState
            .filter { !it.screen.isEmpty }
            .combine(invalidationState){ project, _ ->
                project
            }
            .map { project ->
                val screen = Bitmap.createBitmap(
                    project.screen.width(),
                    project.screen.height(),
                    Bitmap.Config.RGB_565
                )
                val canvas = Canvas(screen)

                project.layers
                    .filter {
                        it.enabled
                    }
                    .map { layer ->
                        layer.renderBitmap(
                            canvas,
                            project.bounds,
                            Rect(0, 0, project.screen.width(), project.screen.height()),
                            0,
                            1f
                        )
                    }
                BitmapState.Defined(screen)
            }

    val selectedGeometryState = MutableStateFlow<WktGeometry?>(null)
    val markerGeometryState = MutableStateFlow<WktPoint?>(null)

    var poiLayer: XMLLayer? = null
    var trackLayer: XMLLayer? = null
    var currentTrack: WktGeometry? = null
    var sensorJob: Job? = null

    private val _commonState = MutableStateFlow<MainState>(MainState.InitialState)
    private val _sensorState = MutableStateFlow<SensorState>(SensorState.InitialState)

    val permissionState: Flow<Status> = _commonState.map { it.manageGranted }

    val controlState: SharedFlow<ControlState> =
        _sensorState.combine(projectState) { sensors, project ->
            sensors to project
        }.combine(markerGeometryState){ (sensors, project), selection ->
            ControlState(sensors, project, selection)
        }
            .shareIn(viewModelScope, SharingStarted.WhileSubscribed(), 1)

    val buttonState =
        _commonState.map {
            it.buttonState
        }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), ButtonState.InitialState)

    init {
        _commonState.filter {
            it.gpsState
        }.onEach {
            application.locationListener().collect { location ->
                _sensorState.updateFlow(viewModelScope) { it.copy(location = location) }
            }
        }.launchIn(viewModelScope)

        markerGeometryState.map{
            it != null
        }.distinctUntilChanged()
            .onEach {
            if(it) {
                sensorJob = viewModelScope.launch {
                    application.sensorListener().cancellable()
                        .map{
                            it[0]
                        }
                        .windowed(48)
                        .collect {orientationDegrees ->
                        _sensorState.updateFlow(viewModelScope) { it.copy(orientations = OrientationState(orientationDegrees, 0f, 0f)) }
                    }
                }
            } else {
                sensorJob?.cancel()
            }
        }.launchIn(viewModelScope)

        viewModelScope.launch {
//            selectedGeometryState
//                .collect {
//                    it?.let{ point ->
//                        val newCenter =
//                            Screen(_projectState.value.screen, _projectState.value.bounds).toScreen(
//                                point.point
//                            )
//                        val dX = _projectState.value.screen.exactCenterX() - newCenter.x
//                        val dY = _projectState.value.screen.exactCenterY() - newCenter.y
////                        moveScreenBy(-dX.toInt(), dY.toInt())
//                    }
//
//            }
            _sensorState.map {
                it.location
            }
                .filter { it != null }
                .combine(buttonState) { location, buttonState ->
                    location to buttonState
                }.collect { (location, state) ->
                when {
                    (state.writeTrack is TrackState.Started) -> {
                        location?.let {
                            val point =
                                WktPoint(
                                    GILonLat(
                                        location.longitude,
                                        location.latitude,
                                        Projection.WGS84
                                    )
                                )
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
                    state.follow -> {
                        location?.let {
                            projectState.value.let { mapState ->
                                val newCenter =
                                    Screen(mapState.screen, mapState.bounds).toScreen(
                                        GILonLat(location)
                                    )
                                val dX = mapState.screen.exactCenterX() - newCenter.x
                                val dY = mapState.screen.exactCenterY() - newCenter.y
                                val distance = hypot(dX, dY)
                                if (distance > 20) moveScreenBy(-dX.toInt(), dY.toInt())
                            }
                        }
                    }
                }
            }
        }
    }

    fun submitAction(action: Action) {
        when (action) {
            is Action.PermissionAction.GPSEnabled -> {
                _commonState.updateFlow(viewModelScope) { it.copy(gpsState = true) }
            }

            is Action.PermissionAction.ManageFileGranted -> {
                if(_commonState.value.manageGranted != Status.Consumed)
                _commonState.updateFlow(viewModelScope) { it.copy(manageGranted = action.granted) }
            }

            is Action.MapAction.ViewRectChanged -> {
                _projectState.updateFlow(viewModelScope) {
                    val correctBounds = it.adjustBoundsRatio(action.rect)
                    it.copy(screen = action.rect, bounds = correctBounds)
                }
            }

            is Action.MapAction.MoveViewBy -> {
                _commonState.updateFlow(viewModelScope) {
                    it.copy(
                        buttonState = it.buttonState.copy(
                            follow = false
                        )
                    )
                }
                moveScreenBy(action.x, action.y)
            }

            is Action.MapAction.ScaleMapBy -> handleScale(action)

            is Action.MapAction.MoveMapBy -> {}

            is Action.MapAction.Update ->  update()

            is Action.MapAction.ClickAt -> handleClick(action.point)

            is Action.ProjectAction.Load -> {
                if (_commonState.value.manageGranted != Status.Bloked) loadProject(action.source)
            }
            is Action.ProjectAction.AddLayer -> {
                Layer.addLayer(action.source)
                    ?.let { layer ->
                        _projectState.updateFlow(viewModelScope) {
                            it.copy(layers = it.layers.toMutableList().apply {
                                add(layer)
                            })
                        }
                    }
            }

            is Action.ProjectAction.RemoveLayer -> {
                _projectState.updateFlow(viewModelScope) {
                    it.copy(layers = it.layers.toMutableList().apply {
                        remove(action.layer)
                    })
                }
            }

            is Action.ProjectAction.MoveLayer -> {
                _projectState.updateFlow(viewModelScope) {
                    it.copy(layers = it.layers.toMutableList().apply {
                        val from = indexOf(action.from)
                        val to = indexOf(action.to)
                        Collections.swap(this, from, to)
                    })
                }
            }

            is Action.ProjectAction.Save -> {
                _commonState.filter {
                    it.manageGranted != Status.Bloked
                }.onEach {
                    saveProject(_projectState.value)
                }.launchIn(viewModelScope)

            }

            is Action.ProjectAction.NameChanged -> {
                _projectState.updateFlow(viewModelScope) {
                    it.copy(name = action.name)
                }
            }

            is Action.ProjectAction.PathChanged -> {
                _projectState.updateFlow(viewModelScope) {
                    it.copy(saveAs = action.path)
                }
            }

            is Action.ProjectAction.DescriptionChanged -> {
                _projectState.updateFlow(viewModelScope) {
                    it.copy(description = action.description)
                }
            }

            is Action.ProjectAction.VisibilityChanged -> _projectState.updateFlow(viewModelScope) {
                it.copy(layers = it.layers.toMutableList().apply {
                    this[indexOf(action.layer)] = when (action.layer) {
                        is SQLLayer -> action.layer.copy(enabled = action.enabled)
                        is XMLLayer -> action.layer.copy(enabled = action.enabled)
                        else -> action.layer
                    }
                })
            }

            is Action.ProjectAction.RangeChanged -> _projectState.updateFlow(viewModelScope) {
                it.copy(layers = it.layers.toMutableList().apply {
                    this[indexOf(action.layer)] = when (action.layer) {
                        is SQLLayer -> action.layer.copy(
                            rangeFrom = action.from,
                            rangeTo = action.to
                        )
                        is XMLLayer -> action.layer.copy(
                            rangeFrom = action.from,
                            rangeTo = action.to
                        )
                        else -> action.layer
                    }
                })
            }

            is Action.ProjectAction.TypeChanged -> _projectState.updateFlow(viewModelScope) {
                it.copy(layers = it.layers.toMutableList().apply {
                    this[indexOf(action.layer)] = when (action.layer) {
                        is SQLLayer -> action.layer.copy(sqlProjection = action.type)
                        else -> action.layer
                    }
                })
            }

            is Action.ProjectAction.MarkersSourceSelected -> _projectState.updateFlow(viewModelScope) {
                it.copy(
                    layers = it.layers
                    .map { layer ->
                        when (layer) {
                            is XMLLayer -> layer.copy(
                                //ToDo possible to show many layer's markers
                                isMarkersSource = (layer != action.layer) || (layer == action.layer && !layer.isMarkersSource),
                            )
                            else -> layer
                        }
                    }
                )
            }

            is Action.ProjectAction.MarkersSelectionChanged -> {
                markerGeometryState.value = if (action.point != markerGeometryState.value) {
                    markerGeometryState.value?.marker = false
                    action.point.apply { marker = true }
                } else {
                    markerGeometryState.value?.marker = false
                    null
                }
                update()
            }

            is Action.ButtonAction.WriteTrack -> {

                _commonState.value.buttonState.let { state ->
                    when (state.writeTrack) {
                        is TrackState.Stopped -> {
                            val format = SimpleDateFormat("MMM_dd_hh_mm_ss", Locale.ENGLISH)
                            val dateString =
                                format.format(Date(Calendar.getInstance().timeInMillis))
                            val nameString = "${_projectState.value.name}_${dateString}.track"
                            val source =
                                Environment.getExternalStorageDirectory().absolutePath + File.separator + (_projectState.value.name
                                    ?: "Track") + File.separator + nameString
                            _commonState.updateFlow(viewModelScope) {
                                it.copy(
                                    buttonState = it.buttonState.copy(
                                        writeTrack = TrackState.Started(source)
                                    )
                                )
                            }
                            handleCreateTrack()
                        }
                        else -> {
                            _commonState.updateFlow(viewModelScope) {
                                it.copy(
                                    buttonState = it.buttonState.copy(
                                        writeTrack = TrackState.Stopped
                                    )
                                )
                            }
                            handleStopTrack()
                        }
                    }
                }
            }

            is Action.ButtonAction.FollowPosition -> {
                _commonState.updateFlow(viewModelScope) {
                    it.copy(
                        buttonState = it.buttonState.copy(
                            follow = !it.buttonState.follow
                        )
                    )
                }
            }

            is Action.ButtonAction.AddPosition -> {
                handleCreatePoi()
            }

            is Action.GeometryAction.Edit -> {
                _commonState.updateFlow(viewModelScope) {
                    it.copy(
                        buttonState = it.buttonState.copy(
                            editGeometry = !it.buttonState.editGeometry
                        )
                    )
                }
            }

            is Action.GeometryAction.Delete -> {
                _projectState.value.layers.filterIsInstance(XMLLayer::class.java)
                    .map { layer ->
                        layer.geometries.remove(action.geometry)
                    }
                markerGeometryState.value = null
                selectedGeometryState.value = null
                update()
            }
            is Action.GeometryAction.SetPoi -> {
                markerGeometryState.value = if (action.geometry != markerGeometryState.value) {
                    markerGeometryState.value?.marker = false
                    action.geometry.apply { marker = true }
                } else {
                    markerGeometryState.value?.marker = false
                    null
                }
                update()
            }

            is Action.GeometryAction.ChangeSelected -> handleChangeSelected(action.geometry)
        }
    }

    private fun update() = invalidationState.updateFlow(viewModelScope) {!it}

    private fun handleScale(action: Action.MapAction.ScaleMapBy) {
        projectState.value.let { project ->
            val newFocusX =
                project.bounds.left + project.pixelWidth * (action.focus.x - project.screen.left)
            val newFocusY =
                project.bounds.top - project.pixelHeight * (action.focus.y - project.screen.top)

            var newLeft =
                action.focus.x - (action.focus.x - project.screen.left).toDouble() / action.factor
            var newTop =
                action.focus.y - (action.focus.y - project.screen.top).toDouble() / action.factor
            var newRight =
                action.focus.x - (action.focus.x - project.screen.right).toDouble() / action.factor
            var newBottom =
                action.focus.y - (action.focus.y - project.screen.bottom).toDouble() / action.factor

            val pixW = newRight - newLeft
            val pixH = newBottom - newTop

            when {
                pixW / pixH > project.ratio -> {
                    val diff =
                        ((pixW / project.screen.width()) * project.screen.height() - pixH) / 2
                    newTop -= diff
                    newBottom += diff
                }
                pixW / pixH < project.ratio -> {
                    val diff =
                        ((pixH / project.screen.height()) * project.screen.width() - pixW) / 2
                    newLeft -= diff
                    newRight += diff
                }
            }

            val newBounds = Bounds(
                project.bounds.projection,
                newFocusX - (action.focus.x - newLeft) * project.pixelWidth,
                newFocusY + (action.focus.y - newTop) * project.pixelHeight,
                newFocusX - (action.focus.x - newRight) * project.pixelWidth,
                newFocusY + (action.focus.y - newBottom) * project.pixelHeight
            )
            _projectState.updateFlow(viewModelScope) {
                it.copy(
                    bounds = newBounds
                )
            }
        }
    }

    private fun moveScreenBy(x: Int, y: Int) {
        projectState.value.let { mapState ->
            Log.d("TOUCH", "update move " + this)
            if(!buttonState.value.editGeometry) {
                val bounds = Bounds(
                    mapState.bounds.projection,
                    mapState.bounds.left + x * mapState.pixelWidth,
                    mapState.bounds.top + y * mapState.pixelHeight,
                    mapState.bounds.right + x * mapState.pixelWidth,
                    mapState.bounds.bottom + y * mapState.pixelHeight
                )
                _projectState.updateFlow(viewModelScope) {
                    it.copy(
                        bounds = bounds
                    )
                }
            } else {

                selectedGeometryState.value?.let{it as? WktPoint}?.let { geometry ->
                    val projectedBounds = _projectState.value.bounds.reproject(geometry.point.projection)
                    val pixelWidth = projectedBounds.width / _projectState.value.screen.width()
                    val pixelHeight = projectedBounds.height / _projectState.value.screen.height()
                    geometry.point =
                        GILonLat(geometry.point.lon - x * pixelWidth, geometry.point.lat - y * pixelHeight, geometry.point.projection)

                    _projectState.value.layers.filterIsInstance(XMLLayer::class.java)
                                        .firstOrNull { it.geometries.contains(geometry) }?.save()
                    update()
                }

//                _projectState.updateFlow(viewModelScope) { project ->
//                    project.copy(
//                        layers = project.layers.map { layer ->
//                            ((layer as? XMLLayer)?.geometries
//                                ?.firstOrNull {
//                                    it.equals(selectedGeometryState.value)
//                                })
//                                ?.let{
//                                    it as? WktPoint
//                                }?.let{ geometry ->
//                                    val projectedBounds = project.bounds.reproject(geometry.point.projection)
//                                    val pixelWidth = projectedBounds.width / project.screen.width()
//                                    val pixelHeight = projectedBounds.height / project.screen.height()
//
//
//                                    layer.geometries[layer.geometries.indexOf (geometry)] = geometry.copy(point = GILonLat(geometry.point.lon - x * pixelWidth, geometry.point.lat - y * pixelHeight, geometry.point.projection))
//                                layer
//                            } ?: layer
//                        }
//                    )
//                }

            }

        }
    }

    private fun handleCreatePoi() {
        if (poiLayer == null) {
            poiLayer = handleCreatePoiLayer()
        }
        projectState.value.bounds.let {
            val poi = WktPoint(Projection.reproject(it.center, Projection.WGS84))
                .apply {
                    this.attributes.put("Date", DBaseField("Date", CommonUtils.currentTime()))
                    this.attributes.put(
                        "Project",
                        DBaseField("Project", _projectState.value.name ?: "Track")
                    )
                }
            poiLayer?.geometries?.add(poi)
            poiLayer?.save()

        }
        update()
    }

    private fun handleCreateTrack() {
        if (trackLayer == null) {
            trackLayer = handleCreateTrackLayer()
        }

        trackLayer?.let { layer ->
            val format = SimpleDateFormat("MMM_dd_mm_ss", Locale.ENGLISH)
            val dateString = format.format(Date(Calendar.getInstance().timeInMillis))
            val nameString = "${_projectState.value.name}_${dateString}.track"
            val source =
                Environment.getExternalStorageDirectory().absolutePath + File.separator + (_projectState.value.name
                    ?: "Track") + File.separator + nameString
            val output = File(source)
            if (!output.exists()) output.createNewFile()
            currentTrack = WktTrack(source)
                .apply {
                    this.attributes.put("Date", DBaseField("Date", CommonUtils.currentTime()))
                    this.attributes.put(
                        "Project",
                        DBaseField("Project", _projectState.value.name ?: "Track")
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
        }
    }

    private fun handleClick(point: Point){
        val slop = _projectState.value.getScreen().touchArea(point, touchSlop)
        viewModelScope.launch {
            _projectState.value.layers
                .filter { it.enabled }
                .filterIsInstance(XMLLayer::class.java)
                .map { layer ->
                    layer.geometries.filterIsInstance(WktPoint::class.java)
                        .filter{ point ->
                            point.isTouch(slop)
                        }
                }.flatten()
                .firstOrNull()
                ?.let { target ->
                    handleChangeSelected(target)
                }
        }
    }

    private fun handleChangeSelected(geometry: WktGeometry){
        selectedGeometryState.value = if (geometry != selectedGeometryState.value) {
            selectedGeometryState.value?.selected = false
            geometry.apply { selected = true }
        } else {
            selectedGeometryState.value?.selected = false
            null
        }
        update()
    }

    private fun handleCreatePoiLayer(name: String? = null): XMLLayer {
        val format = SimpleDateFormat("MMM_dd_yy", Locale.ENGLISH)
        val dateString = format.format(Date(Calendar.getInstance().timeInMillis))
        val nameString = "${_projectState.value.name}_${dateString}_poi.xml"

        val layer = Layer.createPoiLayer(_projectState.value.name ?: "Track", nameString)
            .also {
                it.save()
            }
        _projectState.updateFlow(viewModelScope) {
            it.copy(layers = it.layers.toMutableList().apply {
                add(layer)
            })
        }
        return layer
    }

    private fun handleCreateTrackLayer(name: String? = null): XMLLayer {
        val format = SimpleDateFormat("MMM_dd_yy", Locale.ENGLISH)
        val dateString = format.format(Date(Calendar.getInstance().timeInMillis))
        val nameString = "${_projectState.value.name}_${dateString}_track.xml"

        val layer = Layer.createTrackLayer(_projectState.value.name ?: "Track", nameString)
            .also {
                it.save()
            }
        _projectState.updateFlow(viewModelScope) {
            it.copy(layers = it.layers.toMutableList().apply {
                add(layer)
            })
        }
        return layer
    }

    private fun loadProject(fileName: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val serializer: Serializer = Persister()
                val mapper = ProjectMapper()
                val input = File(fileName)
                (if (input.exists()) {
                    try {
                        serializer.read(GIPropertiesProject::class.java, input)
                            .let {
                                mapper.mapFrom(it)
                            }
                    } catch (e: Exception) {
                        Project.InitialState
                    }

                } else {
                    Project.InitialState
                })
                    .also {
                        _projectState.emit(it)
                    }
            }
        }
    }

    private fun saveProject(project: Project) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val serializer: Serializer = Persister()
                val mapper = ProjectMapper()
                val output =
                    File("${Environment.getExternalStorageDirectory().absolutePath}/${project.saveAs}")
                if (!output.exists()) output.createNewFile()
                projectState.value.also {
                    mapper.mapTo(it)
                        .also {
                            serializer.write(it, output)
                        }
                }
            }
        }
    }
}