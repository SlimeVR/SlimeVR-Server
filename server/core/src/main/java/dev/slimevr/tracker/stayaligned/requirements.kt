package dev.slimevr.tracker.stayaligned

import dev.slimevr.tracker.TrackerState
import dev.slimevr.tracker.getAllActiveFor
import dev.slimevr.tracker.getFirstActiveFor
import solarxr_protocol.rpc.StayAlignedPart

/**
 * The trackers Stay Aligned needs to correct the yaw of moving trackers: at least one upper body
 * tracker plus both upper legs and both lower legs. Feet are optional and not part of this.
 */
class RequiredTrackers(
	val upperBody: List<TrackerState>,
	val leftUpperLeg: TrackerState,
	val rightUpperLeg: TrackerState,
	val leftLowerLeg: TrackerState,
	val rightLowerLeg: TrackerState,
)

/** The required trackers, or null when some are missing. */
fun requiredTrackers(trackerStates: List<TrackerState>): RequiredTrackers? {
	val upperBody = trackerStates.getAllActiveFor(StayAlignedBodyParts.upperBodyGroup)
	if (upperBody.isEmpty()) return null
	return RequiredTrackers(
		upperBody = upperBody,
		leftUpperLeg = trackerStates.getFirstActiveFor(StayAlignedBodyParts.leftUpperLeg) ?: return null,
		rightUpperLeg = trackerStates.getFirstActiveFor(StayAlignedBodyParts.rightUpperLeg) ?: return null,
		leftLowerLeg = trackerStates.getFirstActiveFor(StayAlignedBodyParts.leftLowerLeg) ?: return null,
		rightLowerLeg = trackerStates.getFirstActiveFor(StayAlignedBodyParts.rightLowerLeg) ?: return null,
	)
}

/** The active trackers of a part of the body. Empty when the user has none. */
fun trackersOf(trackerStates: List<TrackerState>, part: StayAlignedPart): List<TrackerState> = when (part) {
	StayAlignedPart.UPPER_BODY -> trackerStates.getAllActiveFor(StayAlignedBodyParts.upperBodyGroup)
	StayAlignedPart.LEFT_UPPER_LEG -> listOfNotNull(trackerStates.getFirstActiveFor(StayAlignedBodyParts.leftUpperLeg))
	StayAlignedPart.RIGHT_UPPER_LEG -> listOfNotNull(trackerStates.getFirstActiveFor(StayAlignedBodyParts.rightUpperLeg))
	StayAlignedPart.LEFT_LOWER_LEG -> listOfNotNull(trackerStates.getFirstActiveFor(StayAlignedBodyParts.leftLowerLeg))
	StayAlignedPart.RIGHT_LOWER_LEG -> listOfNotNull(trackerStates.getFirstActiveFor(StayAlignedBodyParts.rightLowerLeg))
	StayAlignedPart.LEFT_FOOT -> listOfNotNull(trackerStates.getFirstActiveFor(StayAlignedBodyParts.leftFoot))
	StayAlignedPart.RIGHT_FOOT -> listOfNotNull(trackerStates.getFirstActiveFor(StayAlignedBodyParts.rightFoot))
}

/** Parts without a tracker that Stay Aligned needs. */
fun missingRequiredParts(trackerStates: List<TrackerState>): List<StayAlignedPart> = REQUIRED_PARTS.filter { trackersOf(trackerStates, it).isEmpty() }

private val REQUIRED_PARTS = listOf(
	StayAlignedPart.UPPER_BODY,
	StayAlignedPart.LEFT_UPPER_LEG,
	StayAlignedPart.RIGHT_UPPER_LEG,
	StayAlignedPart.LEFT_LOWER_LEG,
	StayAlignedPart.RIGHT_LOWER_LEG,
)
