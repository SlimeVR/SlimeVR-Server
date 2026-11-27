package dev.slimevr.sentry

import dev.slimevr.config.GlobalConfig
import dev.slimevr.config.effectiveConsent
import io.sentry.Sentry
import io.sentry.protocol.User
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

class ErrorReportingConsentSyncBehaviour(private val globalConfig: GlobalConfig) : ErrorReportingBehaviour {
	override fun observe(receiver: ErrorReportingManager) {
		globalConfig.context.state
			.map { effectiveConsent(it.errorReporting) to it.errorReporting.userId }
			.distinctUntilChanged()
			.onEach { (consent, userId) -> receiver.context.dispatch(ErrorReportingActions.SetConsent(consent, userId)) }
			.launchIn(receiver.context.scope)
	}
}

class ErrorReportingSentryBehaviour : ErrorReportingBehaviour {
	override fun observe(receiver: ErrorReportingManager) {
		receiver.context.state
			.map { it.consent to it.userId }
			.distinctUntilChanged()
			.onEach { (consent, userId) ->
				Sentry.setUser(User().apply { id = userId })
				receiver.gate.apply(consent)
			}
			.launchIn(receiver.context.scope)
	}
}
