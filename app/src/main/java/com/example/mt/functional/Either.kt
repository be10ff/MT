package com.example.mt.functional

sealed class Either<out L, out R> {
    /**
     * Returns `true` if this is a [Right], `false` otherwise.
     * Used only for performance instead of fold.
     */
    internal abstract val isRight: Boolean

    /**
     * Returns `true` if this is a [Left], `false` otherwise.
     * Used only for performance instead of fold.
     */
    internal abstract val isLeft: Boolean

    fun isLeft(): Boolean = isLeft

    fun isRight(): Boolean = isRight

    /**
     * The left side of the disjoint union, as opposed to the [Right] side.
     */
    data class Left<out L> @PublishedApi internal constructor(val left: L) : Either<L, Nothing>() {

        override val isLeft
            get() = true
        override val isRight
            get() = false

        companion object {
            operator fun <L> invoke(left: L): Either<L, Nothing> = Left(left)
        }
    }

    /**
     * The right side of the disjoint union, as opposed to the [Left] side.
     */
    data class Right<out R> @PublishedApi internal constructor(val right: R) :
        Either<Nothing, R>() {

        override val isLeft
            get() = false
        override val isRight
            get() = true

        companion object {
            operator fun <R> invoke(right: R): Either<Nothing, R> = Right(right)
        }
    }

    inline fun <C> either(ifLeft: (L) -> C, ifRight: (R) -> C): C =
        when (this) {
            is Right -> ifRight(right)
            is Left -> ifLeft(left)
        }

    companion object {

        fun <L> left(left: L): Either<L, Nothing> = Left(left)

        fun <R> right(right: R): Either<Nothing, R> = Right(right)

        fun <L, R> cond(test: Boolean, ifTrue: () -> R, ifFalse: () -> L): Either<L, R> =
            if (test) right(ifTrue()) else left(ifFalse())
    }
}
