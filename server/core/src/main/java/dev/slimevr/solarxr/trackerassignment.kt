package dev.slimevr.solarxr

import dev.slimevr.VRServer
import dev.slimevr.tracker.TrackerActions
import io.github.axisangles.ktmath.Quaternion
import solarxr_protocol.datatypes.BodyPart
import solarxr_protocol.rpc.AssignTrackerRequest

class AssignTrackerBehaviour(
	private val server: VRServer,
) : SolarXRBridgeBehaviour {
	override fun observe(receiver: SolarXRBridge) {
		receiver.rpcDispatcher.on<AssignTrackerRequest> { req ->
			val trackerId = req.trackerId ?: return@on
			val tracker = server.getTracker(trackerId.trackerNum.toInt())
				?: return@on

			val bodyPart = req.bodyPosition?.takeIf { it != BodyPart.NONE }
			val mountingOrientation = req.mountingOrientation?.let { Quaternion(it.w, it.x, it.y, it.z) }
			tracker.context.dispatch(
				TrackerActions.Update {
					copy(
						bodyPart = bodyPart,
						customName = req.displayName,
						mountingOrientation = mountingOrientation,
					)
				},
			)
		}
	}
}
