package com.example.mt.model

import com.example.mt.R

sealed class PermissionStatus{
    data class Granted(val message: Int = R.string.permission_status_granted) : PermissionStatus()
    data class Denied(val message: Int = R.string.permission_status_denied) : PermissionStatus()
    data class Bloked(val message: Int = R.string.permission_status_bloked) : PermissionStatus()
}
