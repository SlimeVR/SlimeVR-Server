package dev.slimevr.solarxr.rpc

import dev.slimevr.VRServer
import dev.slimevr.skeleton.BoneId
import dev.slimevr.solarxr.SolarXRBridge
import dev.slimevr.solarxr.SolarXRBridgeBehaviour
import dev.slimevr.tracker.Tracker
import dev.slimevr.tracker.TrackerActions
import dev.slimevr.util.isActive
import io.github.axisangles.ktmath.Quaternion
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

			val boneId = BoneId(req.boneId).takeIf { receiver.registry[it] != null }
			if (boneId != null) {
				server.context.state.value.trackers.values.filter {
					val state = it.context.state.value
					state.id != id.toInt() &&
						state.boneId == boneId &&
						state.status.isActive()
				}.forEach {
					it.context.dispatch(
						TrackerActions.Update { copy(boneId = null) },
					)
				}
			}
			tracker.context.dispatch(
				TrackerActions.Update {
					copy(
						boneId = boneId,
						customName = req.displayName ?: customName,
					)
				},
			)

			// Override default mounting orientation set from changing the assigned bone
			val mountingOrientation = req.mountingOrientation?.let { Quaternion(it.w, it.x, it.y, it.z) }
			if (mountingOrientation != null) {
				tracker.context.dispatch(
					TrackerActions.SetMountingOrientation(mountingOrientation),
				)
			}
		}.launchIn(receiver.context.scope)

		receiver.rpcDispatcher.on<ResetTrackerAssignments> {
			val trackers = server.context.state.value.trackers.values
			val intendedBoneIds = mutableMapOf<BoneId, Tracker>()

			// First unassign all trackers so that we don't have conflicts.
			trackers.forEach { tracker ->
				val intendedBoneId = tracker.context.state.value.intendedBoneId
				if (intendedBoneId != null) {
					intendedBoneIds.putIfAbsent(intendedBoneId, tracker)
				}

				tracker.context.dispatch(
					TrackerActions.Update {
						copy(boneId = null)
					},
				)
			}

			// Then re-assign trackers with intended bones, using the map to ensure
			// we don't try to assign two trackers to the same bone if there were
			// multiple with the same intended bone.
			intendedBoneIds.forEach { (boneId, tracker) ->
				tracker.context.dispatch(
					TrackerActions.Update {
						copy(boneId = boneId)
					},
				)
			}
		}.launchIn(receiver.context.scope)
	}
}
