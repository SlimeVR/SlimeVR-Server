@file:Suppress("ForbiddenImport")

package dev.slimevr.util

import dev.slimevr.AppLogger
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * A safe wrapper around [launch] that ensures unhandled exceptions
 * are explicitly caught and routed to the AppLogger, preventing silent failures.
 *
 * If you need specific error recovery, use a try/catch block inside the coroutine,
 * or provide your own [CoroutineExceptionHandler] in the [context].
 */
@Suppress("ForbiddenImport")
fun CoroutineScope.safeLaunch(
	context: CoroutineContext = EmptyCoroutineContext,
	start: CoroutineStart = CoroutineStart.DEFAULT,
	block: suspend CoroutineScope.() -> Unit,
): Job {
	val handler = context[CoroutineExceptionHandler] ?: CoroutineExceptionHandler { ctx, throwable ->
		val name = ctx[CoroutineName]?.name ?: "UnknownScope"
		AppLogger.coroutines.error(throwable, "Unhandled exception in safeLaunch (scope: $name)")
	}
	//noinspection RAW_LAUNCH_USAGE
	return this.launch(context + handler, start = start, block = block)
}
