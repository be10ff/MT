package com.example.mt.functional

class ErrorHandlerManager {
    fun handle(err: Throwable): Failure = Failure.Internal(cause = err)

    @kotlin.jvm.Throws(java.lang.Exception::class)
    fun handle(err: String): Failure = Failure.Internal(err)
}