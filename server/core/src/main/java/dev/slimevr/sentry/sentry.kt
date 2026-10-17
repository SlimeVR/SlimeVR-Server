package dev.slimevr.sentry

import io.klogging.Level
import io.sentry.Breadcrumb
import io.sentry.Sentry
import io.sentry.SentryAttributes
import io.sentry.SentryLevel
import io.sentry.SentryLogLevel
import io.sentry.logger.SentryLogParameters
import solarxr_protocol.rpc.ErrorReportingConsent

internal class ConsentGate<T>(
	private val capacity: Int,
	private val forward: (T) -> Unit,
) {
	private val lock = Any()
	private var consent = ErrorReportingConsent.UNDECIDED
	private val held = ArrayDeque<T>()

	fun offer(item: T) {
		val send = synchronized(lock) {
			if (consent == ErrorReportingConsent.UNDECIDED) {
				if (held.size >= capacity) held.removeFirst()
				held.addLast(item)
			}
			consent == ErrorReportingConsent.ALLOWED
		}
		if (send) forward(item)
	}

	fun apply(newConsent: ErrorReportingConsent) {
		val toSend = synchronized(lock) {
			consent = newConsent
			val toSend = if (newConsent == ErrorReportingConsent.ALLOWED) held.toList() else emptyList()
			if (newConsent != ErrorReportingConsent.UNDECIDED) held.clear()
			toSend
		}
		toSend.forEach(forward)
	}
}

private fun toSentryLevel(level: Level): SentryLevel = when (level) {
	Level.FATAL -> SentryLevel.FATAL
	Level.ERROR -> SentryLevel.ERROR
	Level.WARN -> SentryLevel.WARNING
	Level.INFO -> SentryLevel.INFO
	else -> SentryLevel.DEBUG
}

fun reportError(throwable: Throwable, tags: Map<String, String> = emptyMap()) {
	Sentry.captureException(throwable) { scope -> tags.forEach { (key, value) -> scope.setTag(key, value) } }
}

fun reportLog(logger: String, level: Level, message: String, stackTrace: String?) {
	Sentry.addBreadcrumb(
		Breadcrumb().apply {
			category = logger
			this.level = toSentryLevel(level)
			this.message = message
		},
	)
	val attributes = buildMap<String, Any> {
		put("logger", logger)
		stackTrace?.let { put("stack_trace", it) }
	}
	Sentry.logger().log(SentryLogLevel.valueOf(level.name), SentryLogParameters.create(SentryAttributes.fromMap(attributes)), message)
}

fun flushErrorReports(timeoutMillis: Long) = Sentry.flush(timeoutMillis)

fun closeErrorReporting() = Sentry.close()
