package dev.slimevr.sentry

import dev.slimevr.AppContextProvider
import io.sentry.EventProcessor
import io.sentry.Hint
import io.sentry.Sentry
import io.sentry.SentryEvent

fun registerErrorReportSnapshots(appContext: AppContextProvider) {
	addReportSnapshot("trackers") {
		val trackers = appContext.server.context.state.value.trackers.values.map { tracker ->
			val state = tracker.context.state.value
			mapOf(
				"id" to state.id,
				"deviceId" to state.deviceId,
				"origin" to state.origin.name,
				"driverName" to state.driverName,
				"imuType" to state.imuType?.name,
				"bodyPart" to state.bodyPart?.name,
			)
		}
		mapOf("count" to trackers.size, "trackers" to trackers)
	}

	addReportSnapshot("devices") {
		val devices = appContext.server.context.state.value.devices.values.map { device ->
			val state = device.context.state.value
			mapOf(
				"id" to state.id,
				"origin" to state.origin.name,
				"boardType" to state.boardType.name,
				"mcuType" to state.mcuType.name,
				"firmwareVersion" to state.firmwareVersion,
				"protocolVersion" to state.protocolVersion,
				"status" to state.status.name,
			)
		}
		mapOf("count" to devices.size, "devices" to devices)
	}

	addReportSnapshot("skeleton") {
		val user = appContext.config.userConfig.context.state.value.data
		mapOf("userHeight" to user.userHeight, "proportions" to user.proportions)
	}

	addReportSnapshot("features") {
		val flags = appContext.featureFlags
		mapOf(
			"steam" to flags.steam,
			"supportsDriver" to flags.supportsDriver,
			"keybindSupport" to flags.keybindSupport.name,
			"solarxrConnections" to appContext.server.context.state.value.solarxr.size,
			"dongles" to appContext.server.context.state.value.dongles.size,
		)
	}
}

private fun addReportSnapshot(key: String, provider: () -> Map<String, Any?>) {
	Sentry.configureScope { scope ->
		scope.addEventProcessor(
			object : EventProcessor {
				override fun process(event: SentryEvent, hint: Hint): SentryEvent {
					event.contexts.put(key, provider())
					return event
				}
			},
		)
	}
}
