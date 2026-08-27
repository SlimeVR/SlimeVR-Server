package dev.slimevr.solarxr

import dev.slimevr.VRServer
import dev.slimevr.tracker.TrackerActions
import dev.slimevr.util.isActive
import io.github.axisangles.ktmath.Quaternion
import solarxr_protocol.datatypes.BodyPart
import solarxr_protocol.rpc.AssignTrackerRequest

class AssignTrackerBehaviour(
	private val server: VRServer,
) : SolarXRBridgeBehaviour {
	override fun observe(receiver: SolarXRBridge) {
		receiver.rpcDispatcher.on<AssignTrackerRequest> { req ->
			val id = req.trackerId
			val tracker = server.getTracker(id.toInt())
				?: return@on

			val bodyPart = req.bodyPosition.takeIf { it != BodyPart.NONE }
			if (bodyPart != null) {
				server.context.state.value.trackers.values.filter {
					val state = it.context.state.value
					state.id != id.toInt() &&
						state.bodyPart == bodyPart &&
						state.status.isActive()
				}.forEach {
					it.context.dispatch(
						TrackerActions.Update { copy(bodyPart = null) },
					)
				}
			}
			tracker.context.dispatch(
				TrackerActions.Update {
					copy(
						bodyPart = bodyPart,
						customName = req.displayName ?: customName,
					)
				},
			)

			// Override default mounting orientation set from changing the bodyPart
			val mountingOrientation = req.mountingOrientation?.let { Quaternion(it.w, it.x, it.y, it.z) }
			if (mountingOrientation != null) {
				tracker.context.dispatch(
					TrackerActions.SetMountingOrientation(mountingOrientation),
				)
			}
		}.launchIn(receiver.context.scope)
	}
}
