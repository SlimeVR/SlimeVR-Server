package dev.slimevr.solarxr.rpc

import dev.slimevr.VRServer
import dev.slimevr.skeleton.bodyPartMap
import dev.slimevr.solarxr.SolarXRBridge
import dev.slimevr.solarxr.SolarXRBridgeBehaviour
import dev.slimevr.tracker.Tracker
import dev.slimevr.tracker.TrackerActions
import dev.slimevr.util.isActive
import io.github.axisangles.ktmath.Quaternion
import solarxr_protocol.datatypes.BodyPart
import solarxr_protocol.rpc.AssignTrackerRequest
import solarxr_protocol.rpc.ResetTrackerAssignments

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

		receiver.rpcDispatcher.on<ResetTrackerAssignments> {
			val trackers = server.context.state.value.trackers.values
			val intendedBodyParts = bodyPartMap<Tracker>()

			// First unassign all trackers so that we don't have conflicts.
			trackers.forEach { tracker ->
				val intendedBodyPart = tracker.context.state.value.intendedBodyPart
				intendedBodyParts.putIfAbsent(intendedBodyPart, tracker)

				tracker.context.dispatch(
					TrackerActions.Update {
						copy(bodyPart = null)
					},
				)
			}

			// Then re-assign trackers with intended body parts, using the map to ensure
			// we don't try to assign two trackers to the same body part if there were
			// multiple with the same intended body part.
			intendedBodyParts.forEach { (bodyPart, tracker) ->
				tracker.context.dispatch(
					TrackerActions.Update {
						copy(bodyPart = bodyPart)
					},
				)
			}
		}.launchIn(receiver.context.scope)
	}
}
