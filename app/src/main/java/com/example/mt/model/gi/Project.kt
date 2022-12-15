package com.example.mt.model.gi

data class Project(
    val name: String?,
    val saveAs: String,
    val description: String?,
    val bounds: Bounds,
    val layers: List<Layer>,
    val markerFile: String,
    val markerSource: String
)