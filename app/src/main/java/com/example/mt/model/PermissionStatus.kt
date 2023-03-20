package com.example.mt.model

import com.example.mt.R

sealed class PermissionStatus(open val permission: String) {
    data class Granted(
        override val permission: String,
        val message: Int = R.string.permission_status_granted
    ) : PermissionStatus(permission)

    data class Denied(
        override val permission: String,
        val message: Int = R.string.permission_status_denied
    ) : PermissionStatus(permission)

    data class Bloked(
        override val permission: String,
        val message: Int = R.string.permission_status_bloked
    ) : PermissionStatus(permission)
}
