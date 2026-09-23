package dev.slimevr.tracker.behaviours

import dev.slimevr.skeleton.SkeletonActions
import dev.slimevr.tracker.Tracker
import dev.slimevr.tracker.TrackerActions
import dev.slimevr.tracker.TrackerBehaviour
import dev.slimevr.util.isActive
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import solarxr_protocol.datatypes.BodyPart
import solarxr_protocol.datatypes.TrackerStatus

class TrackerToSkeletonBehaviour : TrackerBehaviour {
	var lastBodyPartSent: BodyPart? = null

	@OptIn(ExperimentalCoroutinesApi::class)
	override fun observe(receiver: Tracker) {
		receiver.context.state
			.distinctUntilChanged { old, new -> old.status == new.status && old.bodyPart == new.bodyPart }
			.onEach { _ ->
				// Tell the skeleton the tracker has stopped sending data to the last bone it was sending data to.
				lastBodyPartSent?.let {
					val trackerState = receiver.context.state.value
					if ((!trackerState.status.isActive() && trackerState.status != TrackerStatus.OCCLUDED) || trackerState.bodyPart != lastBodyPartSent) {
						receiver.appContext.skeleton.context.dispatch(
							SkeletonActions.DisableBone(it),
						)
						lastBodyPartSent = null
					}
				}
			}
			.flatMapLatest { _ ->
				// We only want trackers that are assigned to a BodyPart and are active
				val activeState = receiver.context.state
					.filter { it.bodyPart != null && it.status.isActive() }

				activeState
					.distinctUntilChanged { old, new -> old.rotation == new.rotation && old.position == new.position && old.pendingSkeletonResets == new.pendingSkeletonResets }
					.onEach { trackerState ->
						trackerState.bodyPart?.let { bodyPart ->
							val pendingResets = trackerState.pendingSkeletonResets
							val bonePoseActions = SkeletonActions.SetBonePose(
								bodyPart,
								trackerState.expectedTps ?: trackerState.tps,
								trackerState.rotation,
								trackerState.acceleration,
								trackerState.position,
							)

							// Send bone data + resets (if any, to prevent allocation)
							if (pendingResets.isEmpty()) {
								receiver.appContext.skeleton.context.dispatch(bonePoseActions)
							} else {
								receiver.appContext.skeleton.context.dispatchAll(
									listOf(bonePoseActions) + pendingResets.map { SkeletonActions.RequestProcessorReset(it) },
								)
								receiver.context.dispatch(TrackerActions.ClearPendingSkeletonResets(pendingResets.size))
							}

							lastBodyPartSent = trackerState.bodyPart
						}
					}
			}
			.launchIn(receiver.context.scope)
	}
}
