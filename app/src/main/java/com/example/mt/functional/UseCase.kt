package com.example.mt.functional

import kotlinx.coroutines.withContext

abstract class UseCase<out Type, in Params> constructor(
    private val dispatchers: AppCoroutineDispatchers
) where Type : Any {
    abstract suspend fun run(params: Params): Either<Failure, Type>
    suspend operator fun invoke(params: Params): Either<Failure, Type> {
        return withContext(dispatchers.io) { run(params) }
    }
}

interface UseCaseParams

object EmptyParams : UseCaseParams