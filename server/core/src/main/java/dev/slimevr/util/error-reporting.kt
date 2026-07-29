package dev.slimevr.util

import dev.slimevr.logging.AppLogger
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineName
import kotlin.coroutines.CoroutineContext

/**
 * Reports every coroutine failure that nothing else handled.
 *
 * Install it in the context of the application's root scope. A [CoroutineExceptionHandler] is
 * looked up in the context of the coroutine that *finishes* the exception, and coroutine context
 * is inherited, so a single handler at the root is found by everything underneath it.
 *
 * It fires for coroutines that finish an exception themselves: a root coroutine, or a direct
 * child of a `SupervisorJob` -- which is what every [dev.slimevr.context.Context] scope is, so
 * every behaviour listener lands here.
 *
 * Cancellation is never reported: coroutines cancelled as a *consequence* of another failure
 * carry a [kotlinx.coroutines.CancellationException] and are not passed to a handler at all.
 */
val appCoroutineExceptionHandler = CoroutineExceptionHandler { context, throwable ->
	AppLogger.coroutines.error(throwable, "Unhandled exception in coroutine (scope: ${context.scopeName})")
}

private val CoroutineContext.scopeName: String
	get() = this[CoroutineName]?.name ?: "unnamed"

/**
 * Routes failures on threads that coroutines do not own -- JNA callbacks, JmDNS, serial reader
 * threads -- into the same log, instead of the JVM dumping them on stderr where nothing collects
 * them.
 *
 * Chains to any previously installed handler. The JVM installs none by default, so normally this
 * replaces the stderr dump rather than adding to it.
 */
fun installUncaughtExceptionReporting() {
	val previous = Thread.getDefaultUncaughtExceptionHandler()
	Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
		AppLogger.coroutines.error(throwable, "Unhandled exception on thread ${thread.name}")
		previous?.uncaughtException(thread, throwable)
	}
}
