package dev.slimevr.tracker.behaviours

import dev.slimevr.skeleton.BoneId
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
import solarxr_protocol.datatypes.TrackerStatus

class TrackerToSkeletonBehaviour : TrackerBehaviour {
	var lastBoneIdSent: BoneId? = null

	@OptIn(ExperimentalCoroutinesApi::class)
	override fun observe(receiver: Tracker) {
		receiver.context.state
			.distinctUntilChanged { old, new -> old.status == new.status && old.boneId == new.boneId }
			.onEach { _ ->
				// Tell the skeleton the tracker has stopped sending data to the last bone it was sending data to.
				lastBoneIdSent?.let {
					val trackerState = receiver.context.state.value
					if ((!trackerState.status.isActive() && trackerState.status != TrackerStatus.OCCLUDED) || trackerState.boneId != lastBoneIdSent) {
						receiver.appContext.skeleton.context.dispatch(
							SkeletonActions.DisableBone(it),
						)
						lastBoneIdSent = null
					}
				}
			}
			.flatMapLatest { _ ->
				// We only want trackers that are assigned to a bone and are active
				val activeState = receiver.context.state
					.filter { it.boneId != null && it.status.isActive() }

				activeState
					.distinctUntilChangedBy { it.rotation to it.position }
					.onEach { trackerState ->
						trackerState.boneId?.let { boneId ->
							receiver.appContext.skeleton.context.dispatchAll(
								listOfNotNull(
									SkeletonActions.SetBoneRotation(boneId, trackerState.rotation),
									SkeletonActions.SetBoneAcceleration(boneId, trackerState.acceleration),
									if (trackerState.position != null) SkeletonActions.SetBonePosition(boneId, trackerState.position) else null,
								),
							)
							lastBoneIdSent = trackerState.boneId
						}
					}
			}
			.launchIn(receiver.context.scope)
	}
}
