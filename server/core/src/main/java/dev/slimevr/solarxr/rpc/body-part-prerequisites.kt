package dev.slimevr.solarxr.rpc

import dev.slimevr.AppContextProvider
import dev.slimevr.VRServer
import dev.slimevr.skeleton.BODY_PART_IK_CHAIN_MAP
import dev.slimevr.skeleton.BodyPartMap
import dev.slimevr.skeleton.inputprocessors.BONE_DIRECT_LINK_SOURCES
import dev.slimevr.skeleton.inputprocessors.SPINE_IMPUTED_PARTS
import dev.slimevr.skeleton.iterateBodyPartHierarchy
import dev.slimevr.skeleton.parentOf
import dev.slimevr.skeleton.targetprocessors.POSITIONAL_IK_TARGETS
import dev.slimevr.solarxr.SolarXRBridge
import dev.slimevr.solarxr.SolarXRBridgeBehaviour
import dev.slimevr.util.allContextStates
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import solarxr_protocol.datatypes.BodyPart
import solarxr_protocol.rpc.BodyPartPrerequisites
import solarxr_protocol.rpc.BodyPartPrerequisitesRequest
import solarxr_protocol.rpc.BodyPartPrerequisitesResponse
import solarxr_protocol.rpc.BodyPartRequirement

private val DIRECT_LINK_SOURCE_OF: BodyPartMap<BodyPart> = BodyPartMap(BONE_DIRECT_LINK_SOURCES.toMap())

// Finger and toe bones. Their processors impute each one from its siblings, so one is never a
// prerequisite for another: only the hand or foot they hang off is.
private val DIGIT_PARTS: Set<BodyPart> = buildSet {
	for (root in listOf(BodyPart.LEFT_HAND, BodyPart.RIGHT_HAND, BodyPart.LEFT_FOOT, BodyPart.RIGHT_FOOT)) {
		iterateBodyPartHierarchy(root, onlyChildren = true).forEach { (_, part) -> add(part) }
	}
}

/** Everything a tracker can be assigned to */
internal val PREREQUISITE_BODY_PARTS: List<BodyPart> =
	BodyPart.entries.filter { it != BodyPart.NONE }

/**
 * Simple object to hold body part and if it has position
 */
internal data class TrackingPoint(val bodyPart: BodyPart?, val hasPosition: Boolean)

private fun trackingPointsFlow(server: VRServer): Flow<List<TrackingPoint>> =
	allContextStates(server.context.state, { it.trackers.values }) { tracker ->
		tracker.context.state.map { TrackingPoint(it.bodyPart, it.position != null) }.distinctUntilChanged()
	}

/**
 * List the bones that are solved by IK
 *
 * TODO: Ahead of the solver. [dev.slimevr.skeleton.targetprocessors.PositionalTargetProcessor] still
 * drops the IK once any arm bone has a tracker, so a controller plus an upper arm leaves the
 * forearm copying the upper arm.
 */
internal fun ikSolvedParts(trackers: List<TrackingPoint>, useTrackerPositions: Boolean): Set<BodyPart> {
	if (!useTrackerPositions) return emptySet()

	return buildSet {
		for ((aiming, target) in POSITIONAL_IK_TARGETS) {
			if (trackers.none { it.bodyPart == aiming && it.hasPosition }) continue
			BODY_PART_IK_CHAIN_MAP[target]?.let { addAll(it) }
		}
	}
}

/**
 * What has to be assigned for a tracker here to track well. Each group holds alternatives, and
 * every group has to be met.
 *
 * Read off the hierarchy and the processors that fill in missing bones, so it follows them.
 */
fun bodyPartPrerequisites(bodyPart: BodyPart, ikSolved: Set<BodyPart> = emptySet()): List<List<BodyPart>> =
	requirementGroups(bodyPart).filterNot { group -> group.all { it in ikSolved } }

private fun requirementGroups(bodyPart: BodyPart): List<List<BodyPart>> {
	val standsAlone = bodyPart in SPINE_IMPUTED_PARTS ||
		bodyPart == BodyPart.HEAD ||
		bodyPart == BodyPart.NECK
	if (standsAlone) return emptyList()

	return limbPrerequisites(bodyPart) + listOf(SPINE_IMPUTED_PARTS)
}

/**
 * Limb segments between this part and the spine, each on its own. One left untracked is guessed
 * from its neighbors, so everything past it hangs off a bone that does not follow the body.
 *
 * Excludes segments that follow something else without a tracker of their own: a shoulder taking
 * the upper chest, and digit bones taking their siblings.
 */
private fun limbPrerequisites(bodyPart: BodyPart): List<List<BodyPart>> {
	val required = mutableListOf<List<BodyPart>>()

	var ancestor = parentOf(bodyPart)
	while (ancestor != null && ancestor !in SPINE_IMPUTED_PARTS && ancestor != BodyPart.NECK) {
		val source = DIRECT_LINK_SOURCE_OF[ancestor]
		val followsWithoutTracker = (source != null && source in SPINE_IMPUTED_PARTS) || ancestor in DIGIT_PARTS
		if (!followsWithoutTracker) required.add(listOf(ancestor))
		ancestor = parentOf(ancestor)
	}

	return required
}

private fun buildResponse(ikSolved: Set<BodyPart>) = BodyPartPrerequisitesResponse(
	parts = PREREQUISITE_BODY_PARTS.map { bodyPart ->
		BodyPartPrerequisites(
			bodyPart = bodyPart,
			requires = bodyPartPrerequisites(bodyPart, ikSolved).map { BodyPartRequirement(anyOf = it) },
		)
	},
)

class BodyPartPrerequisitesBehaviour(
	private val appContext: AppContextProvider,
) : SolarXRBridgeBehaviour {
	override fun observe(receiver: SolarXRBridge) {
		val settings = appContext.config.settings

		val responses = combine(
			trackingPointsFlow(appContext.server),
			settings.context.state
				.map { it.data.skeletonConfig.toggles.useTrackerPositions }
				.distinctUntilChanged(),
			::ikSolvedParts,
		).distinctUntilChanged().map(::buildResponse)

		receiver.rpcDispatcher.on<BodyPartPrerequisitesRequest> {
			receiver.sendRpc(responses.first())
		}.launchIn(receiver.context.scope)

		responses
			.drop(1)
			.onEach(receiver::sendRpc)
			.launchIn(receiver.context.scope)
	}
}
