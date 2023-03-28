package com.example.mt.model

sealed class Status{
    object Granted: Status()
    object Consumed: Status()
    object Bloked: Status()
}
