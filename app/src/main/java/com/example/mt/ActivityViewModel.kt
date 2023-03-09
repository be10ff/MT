package com.example.mt

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.mt.model.gi.Project

class ActivityViewModel(app: Application) : AndroidViewModel(app) {
    val projectState: MutableLiveData<Project> = MutableLiveData()
}