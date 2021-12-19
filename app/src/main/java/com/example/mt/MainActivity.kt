package com.example.mt

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.mt.model.GPSAction
import com.example.mt.model.PermissionStatus
import com.example.mt.model.UIAction
import com.example.mt.ui.main.MainFragment
import com.example.mt.ui.main.MainViewModel
import com.example.mt.ui.main.UIViewModel
import com.nabinbhandari.android.permissions.PermissionHandler
import com.nabinbhandari.android.permissions.Permissions
import java.util.ArrayList

class MainActivity : AppCompatActivity() {

    lateinit var uIViewModel: UIViewModel
    lateinit var mainViewModel: MainViewModel

    private var alertDialog : AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, MainFragment.newInstance())
                .commitNow()
        }
        uIViewModel = ViewModelProvider(this).get(UIViewModel::class.java)
        mainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)

//        if (isPermissionGranted(Manifest.permission.READ_PHONE_STATE)) {
//            viewModel.permissionsFlowCompleted()
//        } else {
//            permissionsFlow.request(Manifest.permission.READ_PHONE_STATE).launchIn(lifecycleScope)
//        }
        subscribeToLocationPermissionListener()
    }

    private fun subscribeToLocationPermissionListener(){
        mainViewModel.locationPermissionStatusLiveData.observe(
            this,
            permissionObserver
        )
    }

    private val permissionObserver = Observer<PermissionStatus> { status ->
        status?.let{
            //updatePermissionCheckUI(status)
            when(status){
                is PermissionStatus.Granted -> hangdleGpsDialog()
                is PermissionStatus.Denied -> showLocationPermissionNeededDialog()
            }
        }
    }

    private val permissionHandler= object: PermissionHandler() {
        override fun onGranted() {
//            TODO("Not yet implemented")
            mainViewModel.submitAction(GPSAction.GPSEnabled(true))
        }

        override fun onDenied(context: Context?, deniedPermissions: ArrayList<String>?) {
            super.onDenied(context, deniedPermissions)
            mainViewModel.submitAction(GPSAction.GPSEnabled(false))
        }
    }

    private fun hangdleGpsDialog() {
        mainViewModel.submitAction(GPSAction.GPSEnabled(true))
    }

    private fun showLocationPermissionNeededDialog() {
        if(alertDialog?.isShowing == true) return

        alertDialog = AlertDialog.Builder(this)
            .setTitle(R.string.permission_required_title)
            .setMessage(R.string.permission_required_message)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                Permissions.check(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    null,
                    permissionHandler
                )
            }
            .create()
        alertDialog?.apply { show() }

    }


}
