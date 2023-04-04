package com.example.mt.model.gi

sealed class LonLatFormat(
    open protected var _internal: Double = 0.0,
    ) {
    abstract val _degrees: Number
    abstract val _minutes: Number
    abstract val _seconds: Number

    abstract fun getDegrees() : Number
    abstract fun setDegrees(value: Number) : Number
    abstract fun getMinutes() : Number
    abstract fun setMinutes(value: Number) : Number
    abstract fun getSeconds() : Number
    abstract fun setSeconds(value: Number) : Number
    abstract fun reset(value: Double)

    var internal: Double
    get() = _internal
    set(value) {
        reset(value)
    }

    val k = 100/60
    override fun toString(): String {
        return "$_degrees  $_minutes  $_seconds"
    }

    data class DD_dddd(override var _internal: Double) :LonLatFormat(_internal) {
        override var _degrees: Double = 0.0
        override var _minutes: Int = 0
        override var _seconds: Int = 0
        init{
            reset(_internal)
        }
        override fun reset(value: Double){
            _internal = value
            _degrees = _internal
            _minutes = 0
            _seconds = 0
        }

        override fun getDegrees(): Double = _internal

        override fun setDegrees(value: Number) : Number {
            _degrees = value.toDouble()
            _internal = value.toDouble()
            return _internal
        }

        override fun getMinutes(): Int = 0

        override fun setMinutes(value: Number) = _internal

        override fun getSeconds(): Int = 0

        override fun setSeconds(value: Number) = _internal

        override fun toString(): String {
//            return "$_degrees°"
            return String.format("%.8f°", _degrees, _minutes)
        }
    }

    data class DD_MMmm(override var _internal: Double) :LonLatFormat(_internal) {
        override var _degrees: Int = 0
        override var _minutes: Double = 0.0
        override var _seconds: Int = 0
        init{
            reset(_internal)
        }

        override fun reset(value: Double){
            _internal = value
            _degrees = _internal.toInt()
            _minutes = ((_internal - _internal.toInt())*60)
            _seconds = 0
        }
        override fun getDegrees(): Number {
            return _internal.toInt()
        }

        override fun setDegrees(value: Number) : Number{
            _internal = value.toInt() + _minutes/60f
            return _internal
        }

        override fun getMinutes(): Number {
//             ((internal - _degrees)*60)
            return _minutes
        }

        override fun setMinutes(value: Number) : Number {
            _minutes = value.toDouble()
            _internal = _degrees + value.toDouble()/60f
            return _internal
        }

        override fun getSeconds(): Number = 0

        override fun setSeconds(value: Number) = 0

        override fun toString(): String {
//            return "$_degrees° $_minutes'"
            return String.format("%3d° %.6f\'", _degrees, _minutes)
        }
    }

    data class DD_MM_SSss(override var _internal: Double) :LonLatFormat(_internal) {

        override var _degrees: Int = 0
        override var _minutes:Int = 0
        override var _seconds: Double = 0.0
        init{
            reset(_internal)
        }

        override fun reset(value: Double){
            _internal = value
            _degrees = _internal.toInt()
            _minutes = ((_internal - _internal.toInt())*60).toInt()
            _seconds = ((_internal - _internal.toInt())*60 - ((_internal - _internal.toInt())*60).toInt())*60
        }

        override fun getDegrees(): Number {
//             internal.toInt()
            return _degrees
        }

        override fun setDegrees(value: Number): Number {
            _degrees = value.toInt()
            _internal = value.toInt() + _minutes/60f +  _seconds/3600f
            return _internal
        }

        override fun getMinutes(): Number {
//             ((internal - _degrees)*60).toInt()
            return _minutes
        }

        override fun setMinutes(value: Number): Number {
            _minutes = value.toInt()
            _internal = _degrees + value.toInt()/60f +  _seconds/3600f
            return _internal
        }

        override fun getSeconds(): Number {
//             ((internal - _degrees)*60 - _minutes)*60
            return _seconds
        }

        override fun setSeconds(value: Number) : Number {
            _seconds = value.toDouble()
            _internal = _degrees + _minutes/60f + value.toDouble()/3600f
            return _internal
        }

        override fun toString(): String {
            return String.format("%3d° %2d\' %.4f\"", _degrees, _minutes, _seconds)
//            return "$_degrees° $_minutes' $_seconds\""
        }
    }

}
