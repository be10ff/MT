package com.example.mt.model.gi

import kotlin.math.roundToInt

sealed class LonLatFormat(
    open var internal: Float = 0f,
    ) {
    abstract val _degrees: Number
    abstract val _minutes: Number
    abstract val _seconds: Number

    abstract fun getDegrees() : Number
    abstract fun setDegrees(value: Number)
    abstract fun getMinutes() : Number
    abstract fun setMinutes(value: Number)
    abstract fun getSeconds() : Number
    abstract fun setSeconds(value: Number)

    val k = 100/60
    override fun toString(): String {
        return "$_degrees  $_minutes  $_seconds"
    }

    data class DD_dddd(override var internal: Float) :LonLatFormat(internal) {
        override var _degrees: Float = 0f
        override var _minutes: Int = 0
        override var _seconds: Int = 0
        init{
            set(internal)
        }
        fun set(value: Float){
            internal = value
            _degrees = internal
            _minutes = 0
            _seconds = 0
        }

        override fun getDegrees(): Float = internal

        override fun setDegrees(value: Number) {
            _degrees = value.toFloat()
            internal = value.toFloat()
        }

        override fun getMinutes(): Int = 0

        override fun setMinutes(value: Number) { }

        override fun getSeconds(): Int = 0

        override fun setSeconds(value: Number) {}

        override fun toString(): String {
//            return "$_degrees°"
            return String.format("%.8f°", _degrees, _minutes)
        }
    }

    data class DD_MMmm(override var internal: Float) :LonLatFormat(internal) {
        override var _degrees: Int = 0
        override var _minutes:Float = 0f
        override var _seconds: Int = 0
        init{
            set(internal)
        }

        fun set(value: Float){
            internal = value
            _degrees = internal.toInt()
            _minutes = ((internal - _degrees)*60)
            _seconds = 0
        }
        override fun getDegrees(): Number {
            return internal.toInt()
        }

        override fun setDegrees(value: Number) {
            internal = value.toInt() + _minutes/60f
        }

        override fun getMinutes(): Number {
//             ((internal - _degrees)*60)
            return _minutes
        }

        override fun setMinutes(value: Number) {
            _minutes = value.toFloat()
            internal = _degrees + value.toFloat()/60f
        }

        override fun getSeconds(): Number = 0

        override fun setSeconds(value: Number) {}

        override fun toString(): String {
//            return "$_degrees° $_minutes'"
            return String.format("%3d° %.6f\'", _degrees, _minutes)
        }
    }

    data class DD_MM_SSss(override var internal: Float) :LonLatFormat(internal) {

        override var _degrees: Int = 0
        override var _minutes:Int = 0
        override var _seconds: Float = 0f
        init{
            set(internal)
        }

        fun set(value: Float){
            internal = value
            _degrees = internal.toInt()
            _minutes = ((internal - _degrees)*60).toInt()
            _seconds = ((internal - _degrees)*60 - _minutes)*60
        }

        override fun getDegrees(): Number {
//             internal.toInt()
            return _degrees
        }

        override fun setDegrees(value: Number) {
            _degrees = value.toInt()
            internal = value.toInt() + _minutes/60f +  _seconds/3600f
        }

        override fun getMinutes(): Number {
//             ((internal - _degrees)*60).toInt()
            return _minutes
        }

        override fun setMinutes(value: Number) {
            _minutes = value.toInt()
            internal = _degrees + value.toInt()/60f +  _seconds/3600f
        }

        override fun getSeconds(): Number {
//             ((internal - _degrees)*60 - _minutes)*60
            return _seconds
        }

        override fun setSeconds(value: Number) {
            _seconds = value.toFloat()
            internal = _degrees + _minutes/60f + value.toFloat()/3600f
        }

        override fun toString(): String {
            return String.format("%3d° %2d\' %.4f\"", _degrees, _minutes, _seconds)
//            return "$_degrees° $_minutes' $_seconds\""
        }
    }

}
