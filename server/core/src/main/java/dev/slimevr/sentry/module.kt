package dev.slimevr.sentry

import dev.slimevr.config.GlobalConfig
import dev.slimevr.config.effectiveConsent
import dev.slimevr.context.Behaviour
import dev.slimevr.context.Context
import io.sentry.AsyncHttpTransportFactory
import io.sentry.Hint
import io.sentry.Sentry
import io.sentry.SentryAttributeType
import io.sentry.SentryEnvelope
import io.sentry.SentryLogEventAttributeValue
import io.sentry.SentryOptions
import io.sentry.metrics.SentryMetricsParameters
import io.sentry.transport.ITransport
import kotlinx.coroutines.CoroutineScope
import solarxr_protocol.rpc.ErrorReportingConsent
import java.util.UUID

private const val HELD_LIMIT = 500

data class ReportedUsage(
	val name: String,
	val attributes: Map<String, Any?>,
)

data class ErrorReportingState(
	val sessionId: String,
	val consent: ErrorReportingConsent,
	val userId: String,
	val reportedUsages: Set<ReportedUsage>,
)

sealed interface ErrorReportingActions {
	data class SetConsent(val consent: ErrorReportingConsent, val userId: String) : ErrorReportingActions
	data class MarkUsageReported(val usage: ReportedUsage) : ErrorReportingActions
}

typealias ErrorReportingContext = Context<ErrorReportingState, ErrorReportingActions>
typealias ErrorReportingBehaviour = Behaviour<ErrorReportingManager>

internal class HeldEnvelope(val envelope: SentryEnvelope, val hint: Hint, val transport: ITransport)

class ErrorReportingManager(
	val context: ErrorReportingContext,
) {
	internal val gate = ConsentGate<HeldEnvelope>(HELD_LIMIT) { it.transport.send(it.envelope, it.hint) }

	fun startObserving() = context.observeAll(this)

	@Suppress("UnstableApiUsage")
	fun init(
		dsn: String,
		release: String,
		environment: String,
		tags: Map<String, String>,
		initSentry: (configure: (SentryOptions) -> Unit) -> Unit = { configure -> Sentry.init { configure(it) } },
	) {
		if (dsn.isBlank() || release.isBlank()) return
		val sessionId = context.state.value.sessionId

		initSentry { options ->
			options.dsn = dsn
			options.release = release
			options.environment = environment
			options.setTransportFactory { transportOptions, requestDetails ->
				val inner = AsyncHttpTransportFactory().create(transportOptions, requestDetails)
				object : ITransport {
					override fun send(envelope: SentryEnvelope, hint: Hint) = gate.offer(HeldEnvelope(envelope, hint, inner))
					override fun isHealthy() = inner.isHealthy
					override fun flush(timeoutMillis: Long) = inner.flush(timeoutMillis)
					override fun getRateLimiter() = inner.rateLimiter
					override fun close(isRestarting: Boolean) = inner.close(isRestarting)
					override fun close() = inner.close()
				}
			}
			options.isEnableUncaughtExceptionHandler = false
			options.isSendDefaultPii = false
			options.logs.isEnabled = true
			options.logs.beforeSend = SentryOptions.Logs.BeforeSendLogCallback { log ->
				log.setAttribute("session_id", SentryLogEventAttributeValue(SentryAttributeType.STRING, sessionId))
				log
			}
		}
		tags.forEach { (key, value) -> Sentry.setTag(key, value) }
		Sentry.setTag("session_id", sessionId)
	}

	fun reportUsage(name: String, attributes: Map<String, Any?> = emptyMap()) {
		val present = attributes.mapNotNull { (key, value) -> value?.let { key to it } }.toMap()
		Sentry.metrics().count(name, 1.0, null, SentryMetricsParameters.create(present))
	}

	fun reportUsageOncePerSession(name: String, attributes: Map<String, Any?> = emptyMap()) {
		val usage = ReportedUsage(name, attributes)
		if (usage in context.state.value.reportedUsages) return
		context.dispatch(ErrorReportingActions.MarkUsageReported(usage))
		reportUsage(name, attributes)
	}

	companion object {
		fun create(scope: CoroutineScope, globalConfig: GlobalConfig): ErrorReportingManager {
			val stored = globalConfig.context.state.value.errorReporting
			val context = Context.create(
				initialState = ErrorReportingState(
					sessionId = UUID.randomUUID().toString(),
					consent = effectiveConsent(stored),
					userId = stored.userId,
					reportedUsages = emptySet(),
				),
				scope = scope,
				reducer = ::reduce,
				behaviours = listOf(ErrorReportingConsentSyncBehaviour(globalConfig), ErrorReportingSentryBehaviour()),
				name = "ErrorReportingManager",
			)
			return ErrorReportingManager(context)
		}
	}
}
