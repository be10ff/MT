package com.example.mt.model.gi

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint

data class VectorStyle(
    val pen: Paint,
    val brush: Paint,
    val opacity: Int,
    val image: Bitmap,
    val imageSelected: Bitmap,
    val marker: Bitmap
) {
    companion object {
        val fill = Paint().apply {
            color = Color.BLUE
            style = Paint.Style.FILL
        }

        val fillSelected = Paint().apply {
            color = Color.RED
            style = Paint.Style.FILL
        }

        val fillMarker = Paint().apply {
            color = Color.MAGENTA
            style = Paint.Style.FILL
        }

        val line = Paint().apply {
            color = Color.RED
            style = Paint.Style.STROKE
            strokeWidth = 3f
        }
        //        val point = BitmapFactory.decodeResource(Resources.getSystem(), R.drawable.measure_point)
        val bitmap = Bitmap.createBitmap(
            48,
            48,
            Bitmap.Config.ARGB_8888
        ).apply {
            Canvas(this).drawCircle(24f, 24f, 24f, fill)
        }
        val bitmapSelected = Bitmap.createBitmap(
            48,
            48,
            Bitmap.Config.ARGB_8888
        ).apply {
            Canvas(this).drawCircle(24f, 24f, 24f, fillSelected)
        }

        val marker = Bitmap.createBitmap(
            48,
            48,
            Bitmap.Config.ARGB_8888
        ).apply {
            Canvas(this).drawCircle(24f, 24f, 24f, fillMarker)
        }

        val default = VectorStyle(line, fill, 0, bitmap, bitmapSelected, marker)

    }
}
