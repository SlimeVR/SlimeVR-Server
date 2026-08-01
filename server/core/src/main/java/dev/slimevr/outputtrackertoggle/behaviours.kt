package dev.slimevr.outputtrackertoggle

import dev.slimevr.config.OutputTrackersConfig
import dev.slimevr.device.DeviceOrigin
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import solarxr_protocol.datatypes.BodyPart
import solarxr_protocol.datatypes.TrackerStatus
import kotlin.collections.filter
import kotlin.collections.map

class OutputTrackerToggleBasicBehaviour : OutputTrackerToggleBehaviour {
	private val automaticToggleToTracker = mapOf(
		BodyPart.UPPER_CHEST to setOf(BodyPart.UPPER_CHEST, BodyPart.CHEST),
		BodyPart.LEFT_UPPER_ARM to setOf(BodyPart.LEFT_UPPER_ARM, BodyPart.LEFT_LOWER_ARM),
		BodyPart.RIGHT_UPPER_ARM to setOf(BodyPart.RIGHT_UPPER_ARM, BodyPart.RIGHT_LOWER_ARM),
		BodyPart.HIP to setOf(BodyPart.HIP, BodyPart.WAIST, BodyPart.CHEST, BodyPart.UPPER_CHEST),
		BodyPart.LEFT_UPPER_LEG to setOf(BodyPart.LEFT_UPPER_LEG),
		BodyPart.RIGHT_UPPER_LEG to setOf(BodyPart.RIGHT_UPPER_LEG),
		BodyPart.LEFT_FOOT to setOf(BodyPart.LEFT_FOOT, BodyPart.LEFT_LOWER_LEG, BodyPart.LEFT_UPPER_LEG),
		BodyPart.RIGHT_FOOT to setOf(BodyPart.RIGHT_FOOT, BodyPart.RIGHT_LOWER_LEG, BodyPart.RIGHT_UPPER_LEG),
	)

	fun determineAutomaticOutputTrackers(
		config: OutputTrackersConfig,
		fineBodyParts: Set<BodyPart?>,
	): List<BodyPart> {
		val automaticTrackers = automaticToggleToTracker
			.filterValues { it.any { bp -> bp in fineBodyParts } }
			.keys
		// Hands aren't toggled automatically
		val handTrackers = config.trackers.filter {
			it == BodyPart.LEFT_HAND || it == BodyPart.RIGHT_HAND
		}
		return (automaticTrackers + handTrackers).toList()
	}

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
			.flatMapLatest { config ->
				if (!config.automaticTrackerToggle) return@flatMapLatest emptyFlow()

				receiver.server.context.state
					.map { it.trackers.values }
					.flatMapLatest { trackers ->
						// Tracker state emits on every rotation packet, but only bodyPart/status matter here.
						// Dedup per tracker first, or combine gets resumed once per packet per tracker.
						combine(
							trackers.map { tracker ->
								tracker.context.state.distinctUntilChanged { a, b -> a.bodyPart == b.bodyPart && a.status == b.status }
							},
						) { states ->
							states.filter { (it.status == TrackerStatus.OK || it.status == TrackerStatus.SLEEPING) && it.origin != DeviceOrigin.DRIVER && it.origin != DeviceOrigin.VRC }
								.map { it.bodyPart }
								.toSet()
						}.distinctUntilChanged()
					}
					.map { fineBodyParts -> config to fineBodyParts }
			}
			.onEach { (config, fineBodyParts) ->
				// Get the output trackers based on body parts
				val outputTrackers = determineAutomaticOutputTrackers(config, fineBodyParts)

				// Update state
				receiver.context.dispatch(OutputTrackerToggleActions.SetOutputTrackers(outputTrackers))
			}
			.launchIn(receiver.context.scope)
	}

	override fun reduce(state: OutputTrackerToggleState, action: OutputTrackerToggleActions) = when (action) {
		is OutputTrackerToggleActions.SetOutputTrackers -> {
			state.copy(trackers = action.trackers)
		}
	}
}
