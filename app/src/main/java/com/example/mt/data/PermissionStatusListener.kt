package com.example.mt.data

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.lifecycle.LiveData
import com.example.mt.model.PermissionStatus

class PermissionStatusListener (
    private val context: Context,
    private val permissionToListen: String
) : LiveData<PermissionStatus>() {

    override fun onActive() = handlePermissionCheck()

    private fun handlePermissionCheck() {
        val isPermissionGranted = ActivityCompat.checkSelfPermission(
            context,
            permissionToListen) == PackageManager.PERMISSION_GRANTED

        if(isPermissionGranted) postValue(PermissionStatus.Granted())
        else postValue(PermissionStatus.Denied())
    }
}