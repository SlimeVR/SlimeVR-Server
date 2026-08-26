package dev.slimevr.tracker.behaviours

import dev.slimevr.logging.AppLogger
import dev.slimevr.tracker.Motion
import dev.slimevr.tracker.Tracker
import dev.slimevr.tracker.TrackerActions
import dev.slimevr.tracker.TrackerBehaviour
import dev.slimevr.util.timeSource
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class TrackerBasicBehaviour : TrackerBehaviour {
	override fun observe(receiver: Tracker) {
		observeCalibration(receiver)
		observeTps(receiver)
	}

	/**
	 * Refreshes the tracker's rotation whenever calibration gets updated
	 */
	private fun observeCalibration(receiver: Tracker) {
		receiver.context.state
			.distinctUntilChanged { old, new ->
				old.sessionCalibration == new.sessionCalibration &&
					old.restOrientation == new.restOrientation &&
					old.mountingOrientation == new.mountingOrientation
			}
			.onEach {
				// Make sure to send the raw data to have calibration re-apply
				receiver.context.dispatch(TrackerActions.SetRotation(it.rawRotation, it.rawAcceleration, it.rawMagnetometer, newData = false))
			}.launchIn(receiver.context.scope)
	}

	/**
	 * Sets the tracker's Ticks Per Second (TPS) every second.
	 *
	 * One tick = one new rotation data
	 */
	private fun observeTps(receiver: Tracker) {
		receiver.context.scope.launch {
			var mark = timeSource.markNow()
			while (isActive) {
				try {
					delay(1000)
					val elapsed = mark.elapsedNow()
					val trackerState = receiver.context.state.value
					val tps = (trackerState.accumulatedTicks * 1000u).toLong() / elapsed.inWholeMilliseconds

					// Tracker is at rest if it hasn't been updated in the last second
					val updateMotionAction = if (tps == 0L && trackerState.motion != Motion.RESTING) TrackerActions.SetMotion(Motion.RESTING) else null
					receiver.context.dispatchAll(
						listOfNotNull(
							TrackerActions.Update { copy(tps = tps.toUShort(), accumulatedTicks = 0u) },
							updateMotionAction,
						),
					)

					mark = timeSource.markNow()
				} catch (e: Exception) {
					AppLogger.coroutines.error(e, "Error in TrackerTPSBehaviour")
				}
			}
		}
	}
}
