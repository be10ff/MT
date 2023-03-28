package com.example.mt.data

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.*
import android.location.Location
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Environment
import android.os.Looper
import android.view.Surface
import android.view.WindowManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.mt.model.PermissionStatus
import com.google.android.gms.location.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*

fun Application.permission(vararg permissionsToListen: String): Flow<PermissionStatus> = flow {
    permissionsToListen
        .fold(true) { acc: Boolean, current: String ->
            acc && ActivityCompat.checkSelfPermission(
                this@permission,
                current
            ) == PackageManager.PERMISSION_GRANTED
        }.let {
            if (it) emit(PermissionStatus.Granted(permissionsToListen[0]))
            else emit(PermissionStatus.Denied(permissionsToListen[0]))
        }
}

fun Application.managePermission() = flow {
    if (SDK_INT >= Build.VERSION_CODES.R) {
        Environment.isExternalStorageManager()
    } else {
        val readRes =
            ContextCompat.checkSelfPermission(this@managePermission, READ_EXTERNAL_STORAGE)
        val writeRes =
            ContextCompat.checkSelfPermission(this@managePermission, WRITE_EXTERNAL_STORAGE)
        readRes == PackageManager.PERMISSION_GRANTED && writeRes == PackageManager.PERMISSION_GRANTED
    }.let {
        if (it) emit(PermissionStatus.Granted(WRITE_EXTERNAL_STORAGE))
        else emit(PermissionStatus.Denied(WRITE_EXTERNAL_STORAGE))
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
@SuppressLint("MissingPermission")
fun Application.locationListener(): Flow<Location> = callbackFlow {
    val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(this@locationListener)

    val locationRequest: LocationRequest = LocationRequest.create().apply {
        interval = 5000
        fastestInterval = 1000
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            for (location in locationResult.locations) trySend(location)
        }
    }

    fusedLocationClient.requestLocationUpdates(
        locationRequest,
        locationCallback,
        Looper.getMainLooper()
    )
    awaitClose { fusedLocationClient.removeLocationUpdates(locationCallback) }
}

@OptIn(ExperimentalCoroutinesApi::class)
fun Application.sensorListener(): Flow<FloatArray> = callbackFlow {

    val magnitometerReading = FloatArray(3)
    val accelerometermeterReading = FloatArray(3)

    val rotationMatrix = FloatArray(9)
    val outRotationMatrix = FloatArray(9)
    val orientationAngles = FloatArray(3)

    val sensorManager: SensorManager = (getSystemService(Context.SENSOR_SERVICE) as SensorManager)


    val listener = object:SensorEventListener{
        override fun onSensorChanged(p0: SensorEvent?) {
            when(p0?.sensor?.type){
                Sensor.TYPE_MAGNETIC_FIELD -> {
                    System.arraycopy(p0.values, 0, magnitometerReading, 0, magnitometerReading.size)
                }

                Sensor.TYPE_ACCELEROMETER -> {
                    System.arraycopy(
                        p0.values,
                        0,
                        accelerometermeterReading,
                        0,
                        accelerometermeterReading.size
                    )
                }
            }

            SensorManager.getRotationMatrix(
                rotationMatrix,
                null,
                accelerometermeterReading,
                magnitometerReading
            )
            val rotation: Int = (getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay.rotation
            var x_axis = SensorManager.AXIS_X
            var y_axis = SensorManager.AXIS_Y

            when(rotation){
                Surface.ROTATION_0 -> {}
                Surface.ROTATION_90 -> {
                    x_axis = SensorManager.AXIS_Y
                    y_axis = SensorManager.AXIS_MINUS_X
                }
                Surface.ROTATION_180 -> {
                    y_axis = SensorManager.AXIS_MINUS_Y
                }
                Surface.ROTATION_270 -> {
                    x_axis = SensorManager.AXIS_MINUS_Y
                    y_axis = SensorManager.AXIS_X
                }
            }
            SensorManager.remapCoordinateSystem(rotationMatrix, x_axis, y_axis, outRotationMatrix)
            SensorManager.getOrientation(outRotationMatrix, orientationAngles)

            trySend(orientationAngles)
        }

        override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        }
    }

    val magneticSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
    val accelerometrSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    sensorManager.registerListener(listener, magneticSensor, SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_UI)
    sensorManager.registerListener(listener, accelerometrSensor, SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_UI)

    awaitClose {
        sensorManager.unregisterListener(listener)
    }
}

