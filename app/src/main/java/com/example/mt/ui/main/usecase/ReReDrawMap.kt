package com.example.mt.ui.main.usecase

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import android.util.Log
import com.example.mt.functional.*
import com.example.mt.map.layer.GILayer
import com.example.mt.model.gi.GIBounds

class ReReDrawMap(
    dispatchers: AppCoroutineDispatchers,
    val handler: ErrorHandlerManager
) : UseCase<Bitmap, ReReDrawMap.Params>(dispatchers) {
    override suspend fun run(params: Params): Either<Failure, Bitmap> {
        return runCatching(handler) {
            Log.d("TOUCH", "ReReDrawMap " + this)
//            Bitmap.createBitmap(params.viewRect.width(), params.viewRect.height(), Bitmap.Config.ARGB_8888)
            params.bitmap
                .apply {
                    eraseColor(Color.WHITE)
                    Canvas(this).let { canvas ->
                        GILayer.sqlTest.redraw(params.bounds, this, 0, 0.0)
                        canvas.drawBitmap(this, params.viewRect, params.viewRect, null)
                    }
                }
        }
    }

    data class Params
        (val bitmap: Bitmap, val bounds: GIBounds, val viewRect: Rect)
}