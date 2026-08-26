package dev.slimevr.tracker.behaviours

import dev.slimevr.skeleton.SkeletonActions
import dev.slimevr.tracker.Tracker
import dev.slimevr.tracker.TrackerBehaviour
import dev.slimevr.util.isActive
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.distinctUntilChangedBy
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
					receiver.appContext.skeleton.context.dispatch(
						SkeletonActions.DisableBone(it),
					)
					lastBodyPartSent = null
				}
			}
			.flatMapLatest { _ ->
				// We only want trackers that are assigned to a BodyPart and are active
				val activeState = receiver.context.state
					.filter { it.bodyPart != null && it.status.isActive() }

				activeState
					.distinctUntilChangedBy { it.rotation to it.position }
					.onEach { trackerState ->
						trackerState.bodyPart?.let { bodyPart ->
							receiver.appContext.skeleton.context.dispatchAll(
								listOfNotNull(
									SkeletonActions.SetBoneRotation(bodyPart, trackerState.rotation),
									if (trackerState.position != null) SkeletonActions.SetBonePosition(bodyPart, trackerState.position) else null,
								),
							)
							lastBodyPartSent = trackerState.bodyPart
						}
					}
			}
			.launchIn(receiver.context.scope)
	}
}
