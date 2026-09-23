package dev.slimevr.tracker.behaviours

import dev.slimevr.tracker.Tracker
import dev.slimevr.tracker.TrackerActions
import dev.slimevr.tracker.TrackerBehaviour
import dev.slimevr.tracker.TrackerState
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class TrackerRotationRefreshBehaviour : TrackerBehaviour {
	override fun observe(receiver: Tracker) {
		receiver.context.state
			.distinctUntilChanged { old, new ->
				old.rotationDirty == new.rotationDirty
			}
			.onEach {
				if (it.rotationDirty) {
					// Make sure to send the raw data to have calibration re-apply
					receiver.context.dispatch(getRotationRefreshAction(it))
				}
			}.launchIn(receiver.context.scope)
	}

	companion object {
		fun getRotationRefreshAction(trackerState: TrackerState) = TrackerActions.SetRotation(trackerState.rawRotation, trackerState.rawAcceleration, trackerState.rawMagnetometer, trackerState.position, refresh = true)
	}
}
