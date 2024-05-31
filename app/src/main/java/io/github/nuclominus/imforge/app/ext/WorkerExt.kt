package io.github.nuclominus.imforge.app.ext

import androidx.work.ListenableWorker.Result
import androidx.work.workDataOf

fun buildResultFailureWith(reason: String): Result =
    Result.failure(workDataOf("reason" to reason))
