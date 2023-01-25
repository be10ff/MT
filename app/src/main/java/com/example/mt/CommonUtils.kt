package com.example.mt

import java.text.SimpleDateFormat
import java.util.*

class CommonUtils {
    companion object {
        fun currentTime(): String {
            val format = SimpleDateFormat("MMM_dd_hh_mm", Locale.ENGLISH)
            return format.format(Date(Calendar.getInstance().timeInMillis))
        }
    }
}