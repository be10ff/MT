package com.example.mt.model

sealed class UIAction {
    object ToggleGps : UIAction()
    object ToggleTracking : UIAction()
    object ToggleFollow : UIAction()
}