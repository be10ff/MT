package com.example.mt.data

import android.content.Context
import androidx.lifecycle.LiveData
import com.example.mt.model.GpsStatus

class GpsStatusListener(private val context: Context): LiveData<GpsStatus>() {
}