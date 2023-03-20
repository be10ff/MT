package com.example.mt.data

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.annotation.SuppressLint
import android.app.Application
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Environment
import android.os.Looper
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

