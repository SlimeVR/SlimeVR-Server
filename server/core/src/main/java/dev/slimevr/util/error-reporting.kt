package dev.slimevr.util

import dev.slimevr.logging.AppLogger
import dev.slimevr.sentry.flushErrorReports
import dev.slimevr.sentry.reportError
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineName
import kotlin.coroutines.CoroutineContext

private const val UNCAUGHT_FLUSH_TIMEOUT_MS = 2000L

/**
 * Reports coroutine failures nothing else handled. Install it in the root scope's context: every
 * [dev.slimevr.context.Context] scope is a `SupervisorJob` child, so behaviour failures land here.
 */
val appCoroutineExceptionHandler = CoroutineExceptionHandler { context, throwable ->
	AppLogger.coroutines.error(throwable, "Unhandled exception in coroutine (scope: ${context.scopeName})")
	reportError(throwable, mapOf("scope" to context.scopeName))
}

private val CoroutineContext.scopeName: String
	get() = this[CoroutineName]?.name ?: "unnamed"

/** Reports failures on threads coroutines don't own (JNA callbacks, JmDNS, serial readers) */
fun installUncaughtExceptionReporting() {
	val previous = Thread.getDefaultUncaughtExceptionHandler()
	Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
		AppLogger.coroutines.error(throwable, "Unhandled exception on thread ${thread.name}")
		reportError(throwable, mapOf("thread" to thread.name))
		flushErrorReports(UNCAUGHT_FLUSH_TIMEOUT_MS)
		previous?.uncaughtException(thread, throwable)
	}
}

fun formatExceptionMessage(prefix: String, throwable: Throwable): String {
	val detail = throwable.message?.takeIf { it.isNotBlank() }
		?: throwable::class.simpleName.orEmpty()
	return if (detail.isBlank()) prefix else "$prefix: $detail"
}
