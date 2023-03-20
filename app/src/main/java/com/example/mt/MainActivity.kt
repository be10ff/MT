package com.example.mt

import android.Manifest
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.mt.model.Action
import com.example.mt.model.PermissionStatus
import com.example.mt.ui.main.FragmentViewModel
import com.example.mt.ui.main.MainFragment
import com.nabinbhandari.android.permissions.PermissionHandler
import com.nabinbhandari.android.permissions.Permissions
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach


class MainActivity : AppCompatActivity() {
    lateinit var fragmentViewModel: FragmentViewModel
    private var locationAlertDialog: AlertDialog? = null
    private var manageFilesAlertDialog: AlertDialog? = null

//    private val activityViewModel: FragmentViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, MainFragment.newInstance())
                .commitNow()
        }
        fragmentViewModel = ViewModelProvider(this).get(FragmentViewModel::class.java)

        subscribeToLocationPermissionListener()
        subscribeToManageFilesPermissionListener()
    }

    override fun onStop() {
        fragmentViewModel.submitAction(Action.ProjectAction.Save)
        super.onStop()
    }

    private fun subscribeToLocationPermissionListener() {
        fragmentViewModel.locationPermissionStatusFlow.onEach { status ->
            when (status) {
                is PermissionStatus.Granted -> hangdleGpsDialog()
                is PermissionStatus.Denied -> showLocationPermissionNeededDialog()
                else -> {}
            }
        }.launchIn(lifecycleScope)
    }

    private fun subscribeToManageFilesPermissionListener() {
        fragmentViewModel.manageFilesPermissionStatusFlow.onEach { status ->
            when (status) {
                is PermissionStatus.Denied -> showManageFilesPermissionNeededDialog()
                else -> {
                    fragmentViewModel.submitAction(Action.PermissionAction.ManageFileGranted(true))
                }
            }
        }.launchIn(lifecycleScope)

    }

    private val locationPermissionHandler = object : PermissionHandler() {
        override fun onGranted() {
            fragmentViewModel.submitAction(Action.PermissionAction.GPSEnabled(true))
        }

        override fun onDenied(context: Context?, deniedPermissions: ArrayList<String>?) {
            super.onDenied(context, deniedPermissions)
            fragmentViewModel.submitAction(Action.PermissionAction.GPSEnabled(false))
        }
    }

    private fun hangdleGpsDialog() {
        fragmentViewModel.submitAction(Action.PermissionAction.GPSEnabled(true))
    }

    private fun showLocationPermissionNeededDialog() {
        if (locationAlertDialog?.isShowing == true) return

        locationAlertDialog = AlertDialog.Builder(this)
            .setTitle(R.string.permission_required_title)
            .setMessage(R.string.location_permission_required_message)
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

    private fun showManageFilesPermissionNeededDialog() {
        if (manageFilesAlertDialog?.isShowing == true) return

        manageFilesAlertDialog = AlertDialog.Builder(this)
            .setTitle(R.string.permission_required_title)
            .setMessage(R.string.manage_files_permission_required_message)
            .setPositiveButton(android.R.string.ok) { _, _ ->
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
                val WRITE_EXTERNAL_STORAGE = grantResults[0] == PackageManager.PERMISSION_GRANTED
                if (WRITE_EXTERNAL_STORAGE) {
                    // perform action when allow permission success
                } else {
                    Toast.makeText(this, "Allow permission for storage access!", Toast.LENGTH_SHORT)
                        .show()
                }
                fragmentViewModel.submitAction(
                    Action.PermissionAction.ManageFileGranted(
                        WRITE_EXTERNAL_STORAGE
                    )
                )
            }

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 2296) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    fragmentViewModel.submitAction(Action.PermissionAction.ManageFileGranted(true))
                }
            }
        }
    }

    companion object {
        val PERMISSION_REQUEST_CODE = 11

        fun getRealPath(context: Context?, uri: Uri): String? {
            return context?.contentResolver?.query(uri, null, null, null, null)
                ?.let { cursor ->
                    cursor.moveToFirst()
                    val index = cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA)
                    val result = cursor.getString(index)
                    cursor.close()
                    result
                }
        }
    }
}
