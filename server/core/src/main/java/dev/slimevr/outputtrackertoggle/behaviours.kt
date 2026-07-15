package dev.slimevr.outputtrackertoggle

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import solarxr_protocol.datatypes.BodyPart
import solarxr_protocol.datatypes.TrackerStatus
import solarxr_protocol.rpc.OutputTrackersSettingsResponse
import kotlin.collections.filter
import kotlin.collections.map

private val automaticToggleToTracker = mapOf(
	BodyPart.CHEST to setOf(BodyPart.UPPER_CHEST, BodyPart.CHEST),
	BodyPart.LEFT_UPPER_ARM to setOf(BodyPart.LEFT_UPPER_ARM, BodyPart.LEFT_LOWER_ARM),
	BodyPart.RIGHT_UPPER_ARM to setOf(BodyPart.RIGHT_UPPER_ARM, BodyPart.RIGHT_LOWER_ARM),
	BodyPart.HIP to setOf(BodyPart.UPPER_CHEST, BodyPart.CHEST, BodyPart.WAIST, BodyPart.HIP),
	BodyPart.LEFT_UPPER_LEG to setOf(BodyPart.LEFT_UPPER_LEG),
	BodyPart.RIGHT_UPPER_LEG to setOf(BodyPart.RIGHT_UPPER_LEG),
	BodyPart.LEFT_FOOT to setOf(BodyPart.LEFT_LOWER_LEG, BodyPart.LEFT_FOOT),
	BodyPart.RIGHT_FOOT to setOf(BodyPart.RIGHT_LOWER_LEG, BodyPart.RIGHT_FOOT),
)

class OutputTrackerToggleBasicBehaviour : OutputTrackerToggleBehaviour {
	@OptIn(ExperimentalCoroutinesApi::class)
	override fun observe(receiver: OutputTrackerToggleManager) {
		receiver.settings.context.state
			.map { it.data.outputTrackersConfig }
			.distinctUntilChanged()
			.onEach { config ->
				if (!config.automaticTrackerToggle) {
					// Manual
					receiver.context.dispatch(OutputTrackerToggleActions.SetOutputTrackers(config.trackers))
				}
			}
			.filter { it.automaticTrackerToggle } // Automatic
			.flatMapLatest { config ->
				receiver.server.context.state
					.map { it.trackers.values }
					.flatMapLatest { trackers ->
						combine(trackers.map { it.context.state }) { states ->
							states.map { it.bodyPart to it.status }
						}
							.distinctUntilChanged()
							.map { trackerStates ->
								trackerStates
									.filter { it.second == TrackerStatus.OK }
									.map { it.first }
									.toSet()
							}
					}
					.map { okBodyParts -> config to okBodyParts }
			}
			.onEach { (config, okBodyParts) ->
				val automaticTrackers = automaticToggleToTracker
					.filterValues { it.any { bp -> bp in okBodyParts } }
					.keys
				// Hands aren't toggled automatically
				val handTrackers = config.trackers.filter {
					it == BodyPart.LEFT_HAND || it == BodyPart.RIGHT_HAND
				}
				val outputTrackers = (automaticTrackers + handTrackers).toList()

				// Update state
				receiver.context.dispatch(OutputTrackerToggleActions.SetOutputTrackers(outputTrackers))

				// Update SolarXR
				receiver.server.sendSolarxrRpc(
					OutputTrackersSettingsResponse(
						automaticTrackerToggle = true,
						trackers = outputTrackers,
						sendDerivedVelocity = config.sendDerivedVelocity,
					),
				)
			}
			.launchIn(receiver.context.scope)
	}

	override fun reduce(state: OutputTrackerToggleState, action: OutputTrackerToggleActions) = when (action) {
		is OutputTrackerToggleActions.SetOutputTrackers -> {
			state.copy(trackers = action.trackers)
		}
	}
}
