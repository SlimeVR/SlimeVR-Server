package dev.slimevr.sentry

fun reduce(state: ErrorReportingState, action: ErrorReportingActions): ErrorReportingState = when (action) {
	is ErrorReportingActions.SetConsent -> state.copy(consent = action.consent, userId = action.userId)
	is ErrorReportingActions.MarkUsageReported -> state.copy(reportedUsages = state.reportedUsages + action.usage)
}
