package dev.slimevr.skeleton.targetprocessors

import dev.slimevr.config.Settings
import dev.slimevr.skeleton.BodyPartMap
import dev.slimevr.skeleton.COMState
import dev.slimevr.skeleton.ComputedSkeleton
import dev.slimevr.skeleton.FLOOR_CALIBRATION_OFFSET
import dev.slimevr.skeleton.IKTargets
import dev.slimevr.skeleton.LockState
import dev.slimevr.skeleton.SKATING_LOCK_ENGAGE_PERCENT
import dev.slimevr.skeleton.SkeletonTargetProcessor
import dev.slimevr.skeleton.VELOCITY_BODY_PARTS
import dev.slimevr.skeleton.VelocityState
import dev.slimevr.skeleton.bodyPartMap
import dev.slimevr.skeleton.centerOfMass
import dev.slimevr.skeleton.computeComState
import dev.slimevr.skeleton.computeLockState
import dev.slimevr.skeleton.computeVelocityState
import dev.slimevr.skeleton.shouldLock
import dev.slimevr.util.timeSource

class SkatingCorrectionTargetProcessor(val settings: Settings) : SkeletonTargetProcessor {
	// Centre of mass
	var comState: COMState? = null

	// Do we need to store this or do we just want velocity?
	//  We can probably just pull the last state
	val velocity: BodyPartMap<VelocityState> = bodyPartMap()
	val lockState: BodyPartMap<LockState> = bodyPartMap()

	override fun process(fk: ComputedSkeleton, ikTargets: IKTargets, floorLevel: Float): IKTargets {
		val skeletonConfig = settings.context.state.value.data.skeletonConfig
		if (!skeletonConfig.toggles.skatingCorrection) return ikTargets

		val curTime = timeSource.markNow()

		// Update centre of mass
		comState = computeComState(
			curTime,
			comState,
			centerOfMass(fk),
		)

		val correctionStrength = skeletonConfig.ratios.skatingCorrectionStrength

		for (bodyPart in VELOCITY_BODY_PARTS) {
			val curBone = fk[bodyPart] ?: continue

			// TODO Pull velocity out into the base skeleton, we need it elsewhere too
			val newVel = computeVelocityState(
				curTime,
				velocity[bodyPart],
				curBone,
			)
			velocity[bodyPart] = newVel

			// Consider locking BodyPart
			val lastState = lockState[bodyPart]
			val wasLocked = lastState?.locked ?: false
			val isLocked = shouldLock(
				newVel,
				if (wasLocked) SKATING_LOCK_ENGAGE_PERCENT else 1f,
				floorLevel + FLOOR_CALIBRATION_OFFSET,
			)

			val activeState = computeLockState(
				wasLocked,
				isLocked,
				curBone.tailPosition,
			)?.also {
				// Track lock state changes
				lockState[bodyPart] = it
				// Otherwise pull the last state
			} ?: lastState ?: continue

			if (activeState.locked) {
				ikTargets[bodyPart] = activeState.position
			}
		}
		return ikTargets
	}
}
