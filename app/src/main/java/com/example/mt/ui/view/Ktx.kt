package com.example.mt.ui.view

import android.content.Context
import android.view.View
import kotlin.math.roundToInt

fun View.offsetY(): Int = context.resources.displayMetrics.heightPixels - measuredHeight
fun View.offsetX(): Int = context.resources.displayMetrics.widthPixels - measuredWidth

fun Context.px2Dp(px: Int): Int = (px / resources.displayMetrics.density).roundToInt()
fun Context.dp2Px(dp: Int): Int = (dp * resources.displayMetrics.density).roundToInt()