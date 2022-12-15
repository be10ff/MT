package com.example.mt.data

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Environment
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import com.example.mt.model.PermissionStatus

class FilesPermissionStatusListener(
    private val context: Context
) : LiveData<PermissionStatus>() {

    override fun onActive() = handlePermissionCheck()

    private fun handlePermissionCheck() {

        val isPermissionGranted = if (SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            val readRes = ContextCompat.checkSelfPermission(context, READ_EXTERNAL_STORAGE)
            val writeRes = ContextCompat.checkSelfPermission(context, WRITE_EXTERNAL_STORAGE)
            readRes == PackageManager.PERMISSION_GRANTED && writeRes == PackageManager.PERMISSION_GRANTED
        }

        if (isPermissionGranted) postValue(PermissionStatus.Granted())
        else postValue(PermissionStatus.Denied())
    }
}