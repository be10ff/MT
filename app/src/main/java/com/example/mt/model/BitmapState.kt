package com.example.mt.model

import android.graphics.Bitmap

sealed class BitmapState {
    object Unknown : BitmapState()
    data class Defined(val bitmap: Bitmap) : BitmapState()
}
