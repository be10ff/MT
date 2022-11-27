package com.example.mt.functional

inline fun <T, R> T.runCatching(
    errorHandler: ErrorHandlerManager,
    block: T.() -> R
): Either<Failure, R> {
    return try {
        Either.Right(block())
    } catch (e: Throwable) {
        if (e is Failure) Either.Left(e)
        else Either.Left(errorHandler.handle(e))
    }
}