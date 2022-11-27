package com.example.mt.ui.main.usecase

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import com.example.mt.functional.AppCoroutineDispatchers
import com.example.mt.functional.Either
import com.example.mt.functional.Failure
import com.example.mt.functional.UseCase
import com.example.mt.map.layer.GILayer
import com.example.mt.model.gi.GIBitmap

class ReDrawMap(
    dispatchers: AppCoroutineDispatchers
) : UseCase<GIBitmap, GIBitmap>(dispatchers) {
    override suspend fun run(params: GIBitmap): Either<Failure, GIBitmap> {
        Canvas(params.bitmap).let { canvas ->
            val rect = canvas.clipBounds
            val tmpBitmap =
                Bitmap.createBitmap(rect.width(), rect.height(), Bitmap.Config.ARGB_8888)
                    .apply {
                        eraseColor(Color.WHITE)
                    }
            params._bounds.let { area ->
                GILayer.sqlTest.redraw(area, tmpBitmap, 0, 0.0)
                canvas.drawBitmap(tmpBitmap, rect, rect, null)
            }
        }
        return Either.right(params)
    }

}