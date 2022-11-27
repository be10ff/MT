package com.example.mt.functional

sealed class Failure(
    message: String?,
    cause: Throwable?
) : Throwable(message ?: cause?.message ?: "Message field is empty", cause) {
    class Internal(message: String? = null, cause: Throwable? = null) : Failure(message, cause)
}
