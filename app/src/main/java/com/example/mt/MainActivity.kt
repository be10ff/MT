package com.example.mt

import android.Manifest
import android.content.Context
import android.location.Location
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.mt.model.GPSAction
import com.example.mt.model.PermissionStatus
import com.example.mt.ui.main.MainFragment
import com.example.mt.ui.main.MainViewModel
import com.nabinbhandari.android.permissions.PermissionHandler
import com.nabinbhandari.android.permissions.Permissions

class MainActivity : AppCompatActivity() {
    lateinit var mainViewModel: MainViewModel
    private var locationAlertDialog: AlertDialog? = null
    private var storageAlertDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, MainFragment.newInstance())
                .commitNow()
        }
        mainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        subscribeToLocationPermissionListener()
        subscribeToReadStoragePermissionListener()
        //Move
        mainViewModel.gpsDataLiveData.observe(this, gpsObserver)
    }

    private fun subscribeToLocationPermissionListener() {
        mainViewModel.locationPermissionStatusLiveData.observe(
            this,
            locationPermissionObserver
        )
    }

    private fun subscribeToReadStoragePermissionListener() {
        mainViewModel.readStoragePermissionStatusLiveData.observe(
            this,
            readStoragePermissionObserver
        )
    }

    private val locationPermissionObserver = Observer<PermissionStatus> { status ->
        status?.let {
            when (status) {
                is PermissionStatus.Granted -> hangdleGpsDialog()
                is PermissionStatus.Denied -> showLocationPermissionNeededDialog()
                else -> {}
            }
        }
    }

    private val readStoragePermissionObserver = Observer<PermissionStatus> { status ->
        status?.let {
            when (status) {
                is PermissionStatus.Denied -> showReadStoragePermissionNeededDialog()
                else -> {}
            }
        }
    }

    private val gpsObserver = Observer<Location> { location ->
        mainViewModel.submitAction(GPSAction.LocationUpdated(location))
    }

    private val locationPermissionHandler = object : PermissionHandler() {
        override fun onGranted() {
            mainViewModel.submitAction(GPSAction.GPSEnabled(true))
        }

        override fun onDenied(context: Context?, deniedPermissions: ArrayList<String>?) {
            super.onDenied(context, deniedPermissions)
            mainViewModel.submitAction(GPSAction.GPSEnabled(false))
        }
    }

    private val storagePermissionHandler = object : PermissionHandler() {
        override fun onGranted() {
            //LoadProj
        }

        override fun onDenied(context: Context?, deniedPermissions: ArrayList<String>?) {
            super.onDenied(context, deniedPermissions)
            finish()
        }
    }

    private fun hangdleGpsDialog() {
        mainViewModel.submitAction(GPSAction.GPSEnabled(true))
    }

    private fun showLocationPermissionNeededDialog() {
        if (locationAlertDialog?.isShowing == true) return

        locationAlertDialog = AlertDialog.Builder(this)
            .setTitle(R.string.permission_required_title)
            .setMessage(R.string.permission_required_message)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                Permissions.check(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    null,
                    locationPermissionHandler
                )
            }
            .create()
        locationAlertDialog?.apply { show() }

    }

    private fun showReadStoragePermissionNeededDialog() {
        if (storageAlertDialog?.isShowing == true) return

        storageAlertDialog = AlertDialog.Builder(this)
            .setTitle(R.string.permission_required_title)
            .setMessage(R.string.permission_required_message)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                Permissions.check(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    null,
                    storagePermissionHandler
                )
            }
            .create()
        storageAlertDialog?.apply { show() }

    }


}
