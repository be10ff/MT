package com.example.mt.data

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.lifecycle.LiveData
import com.example.mt.model.PermissionStatus

class PermissionStatusListener (
    private val context: Context,
    private val permissionsToListen: String
) : LiveData<PermissionStatus>() {

    override fun onActive() = handlePermissionCheck()

    private fun handlePermissionCheck() {

//        val res = permissionsToListen.fold(true){ acc: Boolean, current: String ->
//            acc && ActivityCompat.checkSelfPermission(
//                context,
//                current) == PackageManager.PERMISSION_GRANTED
//        }
        val isPermissionGranted = ActivityCompat.checkSelfPermission(
            context,
            permissionsToListen
        ) == PackageManager.PERMISSION_GRANTED

        if (isPermissionGranted) postValue(PermissionStatus.Granted())
        else postValue(PermissionStatus.Denied())
    }
}