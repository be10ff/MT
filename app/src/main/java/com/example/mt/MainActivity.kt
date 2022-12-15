package com.example.mt

import android.Manifest
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.mt.model.Action
import com.example.mt.model.PermissionStatus
import com.example.mt.ui.main.MainFragment
import com.example.mt.ui.main.MainViewModel
import com.nabinbhandari.android.permissions.PermissionHandler
import com.nabinbhandari.android.permissions.Permissions


class MainActivity : AppCompatActivity() {
    lateinit var mainViewModel: MainViewModel
    private var locationAlertDialog: AlertDialog? = null
    private var storageAlertDialog: AlertDialog? = null
    private var manageFilesAlertDialog: AlertDialog? = null

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
        subscribeToManageFilesPermissionListener()
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

    private fun subscribeToManageFilesPermissionListener() {
        mainViewModel.manageFilesPermissionStatusLiveData.observe(
            this,
            manageFilesPermissionObserver
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

    private val manageFilesPermissionObserver = Observer<PermissionStatus> { status ->
        status?.let {
            when (status) {
                is PermissionStatus.Denied -> showManageFilesPermissionNeededDialog()
                else -> {
                    //todo
                    mainViewModel.submitAction(Action.GPSAction.StorageGranted(true))
//                    this.getDir("gimaps", MODE_WORLD_READABLE&MODE_WORLD_WRITEABLE&MODE_APPEND)
                }
            }
        }
    }

    private val gpsObserver = Observer<Location> { location ->
        mainViewModel.submitAction(Action.GPSAction.LocationUpdated(location))
    }

    private val locationPermissionHandler = object : PermissionHandler() {
        override fun onGranted() {
            mainViewModel.submitAction(Action.GPSAction.GPSEnabled(true))
        }

        override fun onDenied(context: Context?, deniedPermissions: ArrayList<String>?) {
            super.onDenied(context, deniedPermissions)
            mainViewModel.submitAction(Action.GPSAction.GPSEnabled(false))
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
        mainViewModel.submitAction(Action.GPSAction.GPSEnabled(true))
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

    private fun showManageFilesPermissionNeededDialog() {
        if (manageFilesAlertDialog?.isShowing == true) return

        manageFilesAlertDialog = AlertDialog.Builder(this)
            .setTitle(R.string.permission_required_title)
            .setMessage(R.string.permission_required_message)
            .setPositiveButton(android.R.string.ok) { _, _ ->
//                Permissions.check(
//                    this,
////                    ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
//                    MANAGE_EXTERNAL_STORAGE,
//                    null,
//                    storagePermissionHandler
//                )
                requestPermission()
            }
            .create()
        manageFilesAlertDialog?.apply { show() }

    }

    private fun requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                intent.addCategory("android.intent.category.DEFAULT")
                intent.data = Uri.parse(String.format("package:%s", applicationContext.packageName))
                startActivityForResult(intent, 2296)
            } catch (e: Exception) {
                val intent = Intent()
                intent.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
                startActivityForResult(intent, 2296)
            }
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(WRITE_EXTERNAL_STORAGE),
                PERMISSION_REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> if (grantResults.size > 0) {
//                val READ_EXTERNAL_STORAGE = grantResults[0] == PackageManager.PERMISSION_GRANTED
//                val WRITE_EXTERNAL_STORAGE = grantResults[1] == PackageManager.PERMISSION_GRANTED
                val WRITE_EXTERNAL_STORAGE = grantResults[0] == PackageManager.PERMISSION_GRANTED
                if (/*READ_EXTERNAL_STORAGE &&*/ WRITE_EXTERNAL_STORAGE) {
                    // perform action when allow permission success
                } else {
                    Toast.makeText(this, "Allow permission for storage access!", Toast.LENGTH_SHORT)
                        .show()
                }
                mainViewModel.submitAction(Action.GPSAction.StorageGranted(WRITE_EXTERNAL_STORAGE))
            }

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 2296) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    mainViewModel.submitAction(Action.GPSAction.StorageGranted(true))
                }
            }
        }
    }

    companion object {
        val PERMISSION_REQUEST_CODE = 11
    }
}
